package uk.ac.aber.dcs.saw.mfc1;

import java.util.ArrayList;

import uk.ac.aber.dcs.saw.mfc1.nav.Vertex;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DestinationListAdapter extends BaseAdapter {
	
	private ArrayList<Vertex> vertexes;
	private LayoutInflater inflater;
	
	private int selected  = 0;
	public DestinationListAdapter(Context context, ArrayList<Vertex> vertexes) {
		super();
		inflater = (LayoutInflater)context.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		this.vertexes = vertexes;
	}

	public void setSelected(int selected) {
		if (selected < vertexes.size())
			this.selected = selected;
	}
	
	@Override
	public int getCount() {
		return this.vertexes.size();
	}

	@Override
	public Object getItem(int index) {
		return this.vertexes.get(index);
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
	
		view = inflater.inflate(R.layout.destination_list_item, null);
	
		TextView destination = (TextView) view.findViewById(R.id.destination);
		Vertex vertex = vertexes.get(position);
	
		destination.setText(vertex.getFriendlyName());
	
		return view;

	}
	
	public Vertex getCurrentSelected() {
		return this.vertexes.get(selected);
	}
	
}
