package utility.quadTree;

import watford.util.quadtree.Rectangle;

public interface QuadTreeEntry {
	Rectangle getBoundsRect();

	void setParentTree(QuadTree quadTree);
	QuadTree getParentTree();
}
