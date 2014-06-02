package com.example.hackapp;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Device List Adapter is used to hold a collection of projects
 * as they are found, and to present them using the "project_list_item"
 * layout style.
 * 
 * @author Michael Clarke <mfc1@aber.ac.uk>
 * @version 0.1
 */
public class ProjectListAdapter extends BaseAdapter {
	
	private ArrayList<Project> projects;
	private LayoutInflater inflater;
	
	public ProjectListAdapter(Context context) {
		
		super();
		projects = new ArrayList<Project>();
		inflater = (LayoutInflater)context.getSystemService(
									Context.LAYOUT_INFLATER_SERVICE);
		
	}
	
	/**
	 * Adds a new project to the list of projects to be displayed.
	 * 
	 * @param project The project to add to the list.
	 */
	public void addProject(Project project) {
	
		/* We might often get the same project found over and over again.
		 * We don't want to keep adding the project to the list, so, if
		 * it's already in the list, we won't add it again.
		 */
		if (!projects.contains(project))
			projects.add(project);
		
	}
	
	/**
	 * Removes a project from the list of projects to be displayed.
	 * 
	 * @param project The project to remove from the list.
	 */
	public void removeProject(Project project) {
		projects.remove(project);
	}
	
	/**
	 * Gets a count of the number of projects in the list.
	 * 
	 * @return A count of the projects in the visible list.
	 */
	public int getCount() {
		
		return projects.size();
		
	}
	
	/**
	 * Gets a specific project from the list of projects.
	 * 
	 * @return A project from the list of projects.
	 */
	public Object getItem(int position) {
		
		return projects.get(position);
		
	}
	
	/**
	 * Gets the ID of the project at the given position in the list.
	 * 
	 * @param position The position of the project in the list.
	 * 
	 * @return The ID of the project in the list at the given position.
	 */
	public long getItemId(int position) {
		
		return position;
		
	}
	
	/**
	 * Builds the view for the current project. Called per-project to populate
	 * the list of projects.
	 * 
	 * @param position The position of the project in the list.
	 * @param view The view to edit.
	 * @param viewGroup the view Group we're working with.
	 * 
	 * @return A view for the given project to be displayed in the list.
	 */
	public View getView(int position, View view, ViewGroup viewGroup) {
		
		TextView projectName;
		TextView projectDesc;
		TextView teamName;
		TextView teamMembers;
		
		view = inflater.inflate(R.layout.project_list_item, null);
		
		projectName   = (TextView) view.findViewById(R.id.projectName);
		projectDesc   = (TextView) view.findViewById(R.id.projectDesc);
		teamName      = (TextView) view.findViewById(R.id.teamName);
		teamMembers   = (TextView) view.findViewById(R.id.teamMembers);

		Project project = projects.get(position);
		
		projectName.setText(project.getProjectName());
		projectDesc.setText(project.getProjectDescription());
		teamName.setText(project.getTeamName());
		
		String tm = "";
		ArrayList<String> projectMembers = project.getProjectMembers();
		for (int i = 0; i < projectMembers.size(); i++) {
			
			tm += projectMembers.get(i);
			if (i < projectMembers.size() - 1) tm += ", ";
			
		}
		teamMembers.setText(tm);
		
		return view;

	}
	
	/**
	 * Gets a list of all the projects in the visible list.
	 * 
	 * @return A list of all projects.
	 */
	public ArrayList<Project> getAll() {
		return this.projects;
	}

	/**
	 * Clears the list of all projects.
	 */
	public void clear() {
		projects.clear();
		
	}
	
}
