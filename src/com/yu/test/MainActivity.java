package com.yu.test;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;

import com.yu.pietable.PieTable;
import com.yu.utils.EntyExpenses;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		List<EntyExpenses> list = new ArrayList<EntyExpenses>();
		list.add(new EntyExpenses("����", 20));
		list.add(new EntyExpenses("����", 30));
		list.add(new EntyExpenses("�Ǻ�", 30));
		list.add(new EntyExpenses("���", 40)); 
		list.add(new EntyExpenses("���", 40)); 
		list.add(new EntyExpenses("���", 40)); 
		list.add(new EntyExpenses("���", 40)); 
		
		PieTable pieTable = (PieTable) findViewById(R.id.pieTable);
		pieTable.initi(list);
		
	}
	
}
