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
			
			//弹对话框
			
			showCustomDialog(appInfoEntityList.get(position));
		}
		
	};
	
	
	/*自定义对话框*/  
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
				// 显示下载对话框
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
		 * 运行在UI线程中，在调用doInBackground()之前执行
		 */
		@Override
		protected void onPreExecute() {
			
			waitDialog = ProgressDialog.show(MainActivity.this, null ,
					 "正在加载应用，请稍后 ... ", true);
			waitDialog.setCancelable(true);
			setDialogFontSize(waitDialog,30);
		}

		/**
		 * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
		 * 
		 * @return
		 */
		@Override
		protected Integer doInBackground(Void... params) {
			initAppInfoData();
			return null;
		}

		/**
		 * 运行在ui线程中，在doInBackground()执行完毕后执行
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
		 * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
		 */
		@Override
		protected void onProgressUpdate(Integer... values) {
			
		}
	}
	
	
	/**
	 * 初始化app信息数据
	 */
	private void initAppInfoData(){
		if(appInfoEntityList!=null){
			appInfoEntityList.clear();
		}
		
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		List<AppInfoEntity> dianshijuInfos = new ArrayList<AppInfoEntity>();
		System.out.println("haohaohaohao ");
		try {
			// .通过该工厂类产生一个SAX的解析类SAXParser
			// 从SAXParser中得到一个XMLReader实例
			XMLReader xmlReader = saxParserFactory.newSAXParser()
					.getXMLReader();

			XmlPaser dsjTypeXmlPaser=new XmlPaser(dianshijuInfos);
//			DsjTypeXmlPaser dsjTypeXmlPaser = new DsjTypeXmlPaser(
//					dianshijuInfos);
			// 将dsjTypeXmlPaser注册到XMLReader中
			xmlReader.setContentHandler(dsjTypeXmlPaser);
			String xml = HttpDownloadML(xmlUrl);// 获取电视剧分类信息xml
			// 将xml资源变成一个JAVA可处理的InputStream留，解析开始
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
	
	// 解析数据
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

    
    
    /* 下载中 */
	private static final int DOWNLOAD = 1;
	/* 下载结束 */
	private static final int DOWNLOAD_FINISH = 2;
	/* 保存解析的XML信息 */
	HashMap<String, String> mHashMap;
	/* 下载保存路径 */
	private String mSavePath;
	/* 记录进度条数量 */
	private int progress;
	/* 是否取消更新 */
	private boolean cancelUpdate = false;
    
    /* 更新进度条 */
	private ProgressBar mProgress;
	private Dialog mDownloadDialog;
    
	private  static  String downloadUrl=null;
	private static String downloadAppName=null;
    /**
	 * 显示软件下载对话框
	 */
	private void showDownloadDialog()
	{
		// 构造软件下载对话框
		AlertDialog.Builder builder = new Builder(MainActivity.this);
		builder.setTitle(R.string.soft_updating);
		// 给下载对话框增加进度条
		final LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
		View v = inflater.inflate(R.layout.softupdate_progress, null);
		mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
		builder.setView(v);
		// 取消更新
		builder.setNegativeButton(R.string.soft_update_cancel, new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				// 设置取消状态
				
			}
		});
		mDownloadDialog = builder.create();
		mDownloadDialog.show();
		// 现在文件
		downloadApk();
	}

	/**
	 * 下载apk文件
	 */
	private void downloadApk()
	{
		// 启动新线程下载软件
		new downloadApkThread().start();
	}

	/**
	 * 下载文件线程
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
				// 判断SD卡是否存在，并且是否具有读写权限
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
				{
					// 获得存储卡的路径
					String sdpath = Environment.getExternalStorageDirectory() + "/";
					mSavePath = sdpath + "download";
					URL url = new URL(downloadUrl);
					// 创建连接
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.connect();
					// 获取文件大小
					int length = conn.getContentLength();
					// 创建输入流
					InputStream is = conn.getInputStream();

					File file = new File(mSavePath);
					// 判断文件目录是否存在
					if (!file.exists())
					{
						file.mkdir();
					}
					File apkFile = new File(mSavePath, downloadAppName);
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					// 缓存
					byte buf[] = new byte[1024];
					// 写入到文件中
					do
					{
						int numread = is.read(buf);
						count += numread;
						// 计算进度条位置
						progress = (int) (((float) count / length) * 100);
						// 更新进度
						mHandler.sendEmptyMessage(DOWNLOAD);
						if (numread <= 0)
						{
							// 下载完成
							mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
							break;
						}
						// 写入文件
						fos.write(buf, 0, numread);
					} while (!cancelUpdate);// 点击取消就停止下载.
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
			// 取消下载对话框显示
			mDownloadDialog.dismiss();
		}
	};

	/**
	 * 安装APK文件
	 */
	private void installApk()
	{
		File apkfile = new File(mSavePath, downloadAppName);
		if (!apkfile.exists())
		{
			return;
		}
		// 通过Intent安装APK文件
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
			
			// 正在下载
			case DOWNLOAD:
				// 设置进度条位置
				mProgress.setProgress(progress);
				break;
			case DOWNLOAD_FINISH:
				// 安装文件
				installApk();
				break;
			default:
				break;
			}
		};
	};

}
