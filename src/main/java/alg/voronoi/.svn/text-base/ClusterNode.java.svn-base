/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is HIVE .
 *
 * The Initial Developer of the Original Code is
 * Greg Ross.
 * Portions created by the Initial Developer are Copyright (C) 2000-2004
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s): Matthew Chalmers <matthew@dcs.gla.ac.uk>
 *                 Alistair Morrison <morrisaj@dcs.gla.ac.uk>
 *                 Greg Ross <gr@dcs.gla.ac.uk>
 *           		
 *	
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */
/**
 * Algorithmic testbed
 *
 * ClusterNode: this class represents the node of a dendrogram created
 * via Voronoi polygon clustering
 *
 *  @author Greg Ross
 */
 
package alg.voronoi;

import java.util.ArrayList;

public class ClusterNode implements java.io.Serializable
{
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	ClusterNode parent;
	ArrayList children = new ArrayList();
	int level;
	int nodeType;
	private ArrayList cluster = null;
	
	public static final int LEAF_NODE = 0;
	public static final int INTERMEDIATE_NODE = 1;
	public static final int ROOT_NODE = 2;
	
	public ClusterNode(ClusterNode parent, int level, int nodeType)
	{
		this.parent = parent;
		this.level = level;
		this.nodeType = nodeType;
	}
	
	// Accessor methods
	
	public void addChild(ClusterNode child)
	{
		children.add(child);
	}
	
	public ArrayList getChildren()
	{
		return children;
	}
	
	public ClusterNode getChild(int index)
	{
		return (ClusterNode)children.get(index);
	}
	
	public int getLevel()
	{
		return level;
	}
	
	public ClusterNode getParent()
	{
		return parent;
	}
	
	public int getNodeType()
	{
		return nodeType;
	}
	
	public void setNodeType(int nodeType)
	{
		this.nodeType = nodeType;
	}
	
	public ArrayList getCluster()
	{
		return cluster;
	}
	
	public void setCluster(ArrayList cluster)
	{
		this.cluster = cluster;
	}
}
