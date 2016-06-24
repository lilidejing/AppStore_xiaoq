package com.csw.appstore_txcz;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.csw.appstore_txcz_qq.R;
import com.csw.entity.AppInfoAdapter;
import com.csw.entity.AppInfoEntity;
import com.csw.sax.HttpDownloader;
import com.csw.sax.XmlPaser;
import com.szy.update.update_main;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    
	private GridView appGridView;
	AppInfoAdapter appInfoAdapter;
	
	ProgressDialog waitDialog;
	private static List<AppInfoEntity> appInfoEntityList=new ArrayList<AppInfoEntity>();
	
//	private static String xmlUrl="http://www.small-seven.com/market/appinfo.xml";
	private static String xmlUrl="http://www.sssmarket.com/market/appinfo.xml";
	
   update_main update = new update_main(MainActivity.this);
	
	private void ui_update() {
		Thread thread = new Thread(runnableup);
		thread.start();
	}
	Runnable runnableup = new Runnable() {
		public void run() {
			try {
				Thread.sleep(10);
				update.update();
				// mUIHandler.sendEmptyMessage(UPDATE_VER);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_main);
		appGridView=(GridView)this.findViewById(R.id.appGridView);
	
		InitDataTask initDataTask = new InitDataTask(
				MainActivity.this);
		initDataTask.execute();
		
		ui_update();
	}

	
		
	OnItemClickListener appOnItemClickListener=new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			
			//���Ի���
			
			showCustomDialog(appInfoEntityList.get(position));
		}
		
	};
	
	
	/*�Զ���Ի���*/  
    private void showCustomDialog(final AppInfoEntity appInfoEntity)  
    {  
        AlertDialog.Builder customDia=new AlertDialog.Builder(MainActivity.this);  
        final View viewDia=LayoutInflater.from(MainActivity.this).inflate(R.layout.appinfo_dialog, null);  

        ImageView appImageView=(ImageView)viewDia.findViewById(R.id.appIconDiaImageView);
        TextView appNameText=(TextView)viewDia.findViewById(R.id.appNameDiaText);
        TextView appVersionText=(TextView)viewDia.findViewById(R.id.appVerisonDiaText);
        TextView appSizeText=(TextView)viewDia.findViewById(R.id.appSizeDiaText);
        
        appImageView.setImageBitmap(appInfoEntity.getAppIcon());
        appNameText.setText(appInfoEntity.getAppName());
        appVersionText.setText(appInfoEntity.getAppVersion());
        appSizeText.setText(appInfoEntity.getAppSize());
        
        customDia.setView(viewDia);  
        customDia.setPositiveButton(MainActivity.this.getResources().getText(R.string.download), new DialogInterface.OnClickListener() {  
              
            @Override  
            public void onClick(DialogInterface dialog, int which) {  
                // TODO Auto-generated method stub  
               
            	dialog.dismiss();
            	downloadUrl=appInfoEntity.getAppUrl();
            	downloadAppName=appInfoEntity.getAppName();
				// ��ʾ���ضԻ���
				showDownloadDialog();	
            }  
        });  
        customDia.create().show();  
    }  
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	class InitDataTask extends AsyncTask<Void, Integer, Integer> {
		private Context context;

		InitDataTask(Context context) {
			this.context = context;
		}

		/**
		 * ������UI�߳��У��ڵ���doInBackground()֮ǰִ��
		 */
		@Override
		protected void onPreExecute() {
			
			waitDialog = ProgressDialog.show(MainActivity.this, null ,
					 "���ڼ���Ӧ�ã����Ժ� ... ", true);
			waitDialog.setCancelable(true);
			setDialogFontSize(waitDialog,30);
		}

		/**
		 * ��̨���еķ������������з�UI�̣߳�����ִ�к�ʱ�ķ���
		 * 
		 * @return
		 */
		@Override
		protected Integer doInBackground(Void... params) {
			initAppInfoData();
			return null;
		}

		/**
		 * ������ui�߳��У���doInBackground()ִ����Ϻ�ִ��
		 */
		@Override
		protected void onPostExecute(Integer integer) {

			if(waitDialog!=null){
				waitDialog.dismiss();
				waitDialog=null;
				
			}
			appInfoAdapter=new AppInfoAdapter(MainActivity.this,appInfoEntityList);
			appGridView.setAdapter(appInfoAdapter);
			appGridView.setOnItemClickListener(appOnItemClickListener);

		}

		/**
		 * ��publishProgress()�������Ժ�ִ�У�publishProgress()���ڸ��½���
		 */
		@Override
		protected void onProgressUpdate(Integer... values) {
			
		}
	}
	
	
	/**
	 * ��ʼ��app��Ϣ����
	 */
	private void initAppInfoData(){
		if(appInfoEntityList!=null){
			appInfoEntityList.clear();
		}
		
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		List<AppInfoEntity> dianshijuInfos = new ArrayList<AppInfoEntity>();
		System.out.println("haohaohaohao ");
		try {
			// .ͨ���ù��������һ��SAX�Ľ�����SAXParser
			// ��SAXParser�еõ�һ��XMLReaderʵ��
			XMLReader xmlReader = saxParserFactory.newSAXParser()
					.getXMLReader();

			XmlPaser dsjTypeXmlPaser=new XmlPaser(dianshijuInfos);
//			DsjTypeXmlPaser dsjTypeXmlPaser = new DsjTypeXmlPaser(
//					dianshijuInfos);
			// ��dsjTypeXmlPaserע�ᵽXMLReader��
			xmlReader.setContentHandler(dsjTypeXmlPaser);
			String xml = HttpDownloadML(xmlUrl);// ��ȡ���Ӿ������Ϣxml
			// ��xml��Դ���һ��JAVA�ɴ����InputStream����������ʼ
			xmlReader.parse(new InputSource(new StringReader(xml)));
			System.out.println("haohaohahaohaoh");
			for (Iterator iterator = dianshijuInfos.iterator(); iterator
					.hasNext();) {
				AppInfoEntity dianshijuInfo = (AppInfoEntity) iterator.next();
				System.out.println(dianshijuInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		appInfoEntityList=dianshijuInfos;

		/* SAXParserFactory spf=SAXParserFactory.newInstance();
	     try {
				SAXParser sp=spf.newSAXParser();
				XmlPaser xmlPar=new XmlPaser();
				InputStream is=this.getClass().getClassLoader().getResourceAsStream("appinfo.xml");
				sp.parse(is, xmlPar);
//				AppInfoEntity appInfoEntityXML=xmlPar.getAppInfoEntity();
				
				appInfoEntityList=xmlPar.getAppInfoEntityList();
		
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} */
	}
	
	// ��������
		private String HttpDownloadML(String urlStr) {
			HttpDownloader httpDownloader = new HttpDownloader();
			String result = httpDownloader.downloadText(urlStr);
			return result;
		}
	
	
	private void setDialogFontSize(Dialog dialog,int size)
    {
        Window window = dialog.getWindow();
        View view = window.getDecorView();
        setViewFontSize(view,size);
    }
    private void setViewFontSize(View view,int size)
    {
        if(view instanceof ViewGroup)
        {
            ViewGroup parent = (ViewGroup)view;
            int count = parent.getChildCount();
            for (int i = 0; i < count; i++)
            {
                setViewFontSize(parent.getChildAt(i),size);
            }
        }
        else if(view instanceof TextView){
            TextView textview = (TextView)view;
            textview.setTextSize(size);
        }
    }

    
    
    /* ������ */
	private static final int DOWNLOAD = 1;
	/* ���ؽ��� */
	private static final int DOWNLOAD_FINISH = 2;
	/* ���������XML��Ϣ */
	HashMap<String, String> mHashMap;
	/* ���ر���·�� */
	private String mSavePath;
	/* ��¼���������� */
	private int progress;
	/* �Ƿ�ȡ������ */
	private boolean cancelUpdate = false;
    
    /* ���½����� */
	private ProgressBar mProgress;
	private Dialog mDownloadDialog;
    
	private  static  String downloadUrl=null;
	private static String downloadAppName=null;
    /**
	 * ��ʾ������ضԻ���
	 */
	private void showDownloadDialog()
	{
		// ����������ضԻ���
		AlertDialog.Builder builder = new Builder(MainActivity.this);
		builder.setTitle(R.string.soft_updating);
		// �����ضԻ������ӽ�����
		final LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
		View v = inflater.inflate(R.layout.softupdate_progress, null);
		mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
		builder.setView(v);
		// ȡ������
		builder.setNegativeButton(R.string.soft_update_cancel, new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				// ����ȡ��״̬
				
			}
		});
		mDownloadDialog = builder.create();
		mDownloadDialog.show();
		// �����ļ�
		downloadApk();
	}

	/**
	 * ����apk�ļ�
	 */
	private void downloadApk()
	{
		// �������߳��������
		new downloadApkThread().start();
	}

	/**
	 * �����ļ��߳�
	 * 
	 * @author coolszy
	 *@date 2012-4-26
	 *@blog http://blog.92coding.com
	 */
	private class downloadApkThread extends Thread
	{
		@Override
		public void run()
		{
			try
			{
				// �ж�SD���Ƿ���ڣ������Ƿ���ж�дȨ��
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
				{
					// ��ô洢����·��
					String sdpath = Environment.getExternalStorageDirectory() + "/";
					mSavePath = sdpath + "download";
					URL url = new URL(downloadUrl);
					// ��������
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.connect();
					// ��ȡ�ļ���С
					int length = conn.getContentLength();
					// ����������
					InputStream is = conn.getInputStream();

					File file = new File(mSavePath);
					// �ж��ļ�Ŀ¼�Ƿ����
					if (!file.exists())
					{
						file.mkdir();
					}
					File apkFile = new File(mSavePath, downloadAppName);
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					// ����
					byte buf[] = new byte[1024];
					// д�뵽�ļ���
					do
					{
						int numread = is.read(buf);
						count += numread;
						// ���������λ��
						progress = (int) (((float) count / length) * 100);
						// ���½���
						mHandler.sendEmptyMessage(DOWNLOAD);
						if (numread <= 0)
						{
							// �������
							mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
							break;
						}
						// д���ļ�
						fos.write(buf, 0, numread);
					} while (!cancelUpdate);// ���ȡ����ֹͣ����.
					fos.close();
					is.close();
				}
			} catch (MalformedURLException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			// ȡ�����ضԻ�����ʾ
			mDownloadDialog.dismiss();
		}
	};

	/**
	 * ��װAPK�ļ�
	 */
	private void installApk()
	{
		File apkfile = new File(mSavePath, downloadAppName);
		if (!apkfile.exists())
		{
			return;
		}
		// ͨ��Intent��װAPK�ļ�
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
		MainActivity.this.startActivity(i);
	}
	
	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			
			// ��������
			case DOWNLOAD:
				// ���ý�����λ��
				mProgress.setProgress(progress);
				break;
			case DOWNLOAD_FINISH:
				// ��װ�ļ�
				installApk();
				break;
			default:
				break;
			}
		};
	};

}
