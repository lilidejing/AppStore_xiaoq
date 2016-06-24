package com.csw.entity;

import java.util.List;
import com.csw.appstore_txcz_qq.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * appinfo  ≈‰∆˜
 * @author lgj
 *
 */
public class AppInfoAdapter extends BaseAdapter {

	private List<AppInfoEntity> appInfoEntityList;
	private LayoutInflater mInfater = null;
	private Context mContext;

	public AppInfoAdapter(Context context, List<AppInfoEntity> appInfoEntityList) {
		this.mContext = context;
		this.appInfoEntityList = appInfoEntityList;
		this.mInfater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return appInfoEntityList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return appInfoEntityList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = null;
		ViewHolder holder = null;
		if (convertView == null || convertView.getTag() == null) {
			view = mInfater.inflate(R.layout.appinfo_item, null);
			holder = new ViewHolder(view);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) convertView.getTag();
		}
		AppInfoEntity appInfoEntity = (AppInfoEntity) getItem(position);
		Bitmap appIconBitmap = appInfoEntity.getAppIcon();
		String appName = appInfoEntity.getAppName();
		holder.appIconView.setImageBitmap(appIconBitmap);
		holder.appNameText.setText(appName);
		return view;

	}

	class ViewHolder {
		ImageView appIconView;
		TextView appNameText;

		public ViewHolder(View view) {
			this.appIconView = (ImageView) view.findViewById(R.id.appitem_icon);
			this.appNameText = (TextView) view.findViewById(R.id.appitem_name);
		}
	}
}
