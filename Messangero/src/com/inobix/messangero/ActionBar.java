package com.inobix.messangero;

import java.util.Calendar;
import java.util.Locale;

import com.inobix.messangero.R;
import com.inobix.messangero.common.IActionActivity;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class ActionBar extends RelativeLayout {
	ListView lvMenu;
	Button btnMenu;
	Button btnSearch;
	
	Button btnMenu1;
	Button btnSearch1;
	
	ProgressBar pbSync;
	RelativeLayout layoutMenu;

	public ActionBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.action_bar, this);
		btnMenu = (Button) view.findViewById(R.id.btnOptions);
		btnMenu1 = (Button) view.findViewById(R.id.btnOptions1);
		btnMenu.setTag(context);
		btnSearch = (Button) view.findViewById(R.id.btnSync);
		btnSearch1 = (Button) view.findViewById(R.id.btnSync1);
		btnSearch.setTag(context);
		btnSearch1.setTag(context);
		pbSync = (ProgressBar) view.findViewById(R.id.pbSync1);

		btnSearch.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				Activity parent = ((Activity) arg0.getTag());
				if (parent != null && (parent instanceof IActionActivity)) 
					((IActionActivity) parent).RightButtonCLicked(arg0);
			}
		});
		
		btnSearch1.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				Activity parent = ((Activity) arg0.getTag());
				if (parent != null && (parent instanceof IActionActivity)) 
					((IActionActivity) parent).RightButton1CLicked(arg0);
			}
		});

		btnMenu.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				Activity parent = ((Activity) arg0.getTag());
				if (parent != null && (parent instanceof IActionActivity))
					((IActionActivity) parent).LeftButtonCLicked(arg0);
			}
		});
		
		
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		SetLeftButtonText(String.valueOf(String.valueOf(dayOfMonth)
				+ " " + String.format(Locale.ENGLISH, "%tB", calendar).substring(0, 3) + "."));

	}

	public void SetLeftButtonText(String text) {
		btnMenu.setText(text);
	}
	
	public void SetLeftButton1Text(String text) {
		btnMenu1.setVisibility(Button.VISIBLE);
		btnMenu1.setText(text);
	}
	
	public void SetRightButton1Text(String text) {
		btnSearch1.setVisibility(Button.VISIBLE);
		btnSearch1.setText(text);
	}

	public void SetRightButtonText(String text) {
		btnSearch.setText(text);
	}

	public void ShowProgressBar() {
		pbSync.setVisibility(ProgressBar.VISIBLE);
		btnSearch.setVisibility(Button.INVISIBLE);
	}

	public void HideProgressBar() {
		pbSync.setVisibility(ProgressBar.INVISIBLE);
		btnSearch.setVisibility(Button.VISIBLE);
	}

}
