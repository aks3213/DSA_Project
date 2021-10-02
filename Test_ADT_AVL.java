import java.util.*;
/* =================================================================
   Node structure
   ================================================================= */

class Node
{
	public String  key;
	public Integer value;
	public int height;

	public Node parent;
	public Node left;
	public Node right;

	public Node(String k, Integer v)
	{
		key = k;
		value = v;

		parent = null;
		left = null;
		right = null;
	}
}


/* ================================================================
   This is BST with balancing "height" (= AVL)

   Each node has a height to determine if the BST is balanced
   ================================================================ */

class ADT_BY_AVL
{
	public Node root;	// References the root node of the BST

	public ADT_BY_AVL()
	{
		root = null;
	}

	/* ================================================================
		findNode(k): find node with key k 

		Return:  reference to (k,v) IF k is in BST
			reference to parent(k,v) IF k is NOT in BST (for put)
	================================================================ */
	public Node findNode(String k)
	{
		Node current;   // Help variable
		Node previous;   // Help variable

		/* --------------------------------------------
			Find the node with key == "k" in the BST
		-------------------------------------------- */
		current = root;  // Always start at the root node
		previous = root;  // Remember the previous node for insertion

		while ( current != null )
		{
			if ( k.compareTo( current.key ) < 0 )
			{
				previous = current;       // Remember prev. node
				current = current.left;  // Continue search in left subtree
			}
			else if ( k.compareTo( current.key ) > 0 )
			{
				previous = current;       // Remember prev. node
				current = current.right; // Continue search in right subtree
			}
			else 
			{
				// Found key in BST 
				return current;
			}
		}

		/* ======================================
			When we reach here, k is NOT in BST
		====================================== */
		return previous;		// Return the previous (parent) node
	}

	/* ================================================================
		get(k): find key k and return assoc. value
	================================================================ */
	public Integer get(String k)
	{
		Node p;   // Help variable

		/* --------------------------------------------
			Find the node with key == "key" in the BST
		-------------------------------------------- */
		p = findNode(k);

		if ( k.equals( p.key ) )
			return p.value;
		else
			return null;
	}

	/* ================================================================
		put(k, v): store the (k,v) pair into the BST

		1. if the key "k" is found in the BST, we replace the val
		that is associated with the key "k"
		1. if the key "k" is NOT found in the BST, we insert
		a new node containing (k, v)
	================================================================ */
	public void put(String k, Integer v)
	{
		Node p;   // Help variable

		/* ----------------------------------------------------------
			Just like linked list, insert in an EMPTY BST
			must be taken care off separately by an if-statement
		---------------------------------------------------------- */
		if ( root == null )
		{  // Insert into an empty BST

			root = new Node( k, v );
			root.height = 1;
			return;
		}

		/* --------------------------------------------
			Find the node with key == "key" in the BST
		-------------------------------------------- */
		p = findNode(k);

		if ( k.equals( p.key ) )
		{
			p.value = v;			// Update value
			return;
		}

		/* --------------------------------------------
			Insert a new node (k,v) under p !!!
		-------------------------------------------- */
		Node q = new Node( k, v );
		q.height = 1;

		q.parent = p;

		if ( k.compareTo( p.key ) < 0 )
			p.left = q;            	// Add q as left child
		else 
			p.right = q;           	// Add q as right child

		/* --------------------------------------------
			Recompute the height of all parent nodes...
		-------------------------------------------- */
		getHeight(p);

		/* --------------------------------------------
			Check for height violation
		-------------------------------------------- */
		Node x, y, z;
		x = y = z = q;      // Start search at q (new node)

		while ( x != null )
		{
			if ( getDiffOfHeight(x.left, x.right) <= 1 )
			{
				z = y;
				y = x;
				x = x.parent;
			}
			else
				break;
		}


		if ( x != null )
		{
			/* --------------------------------------------
				Print tree after insertiom
			-------------------------------------------- */
			System.out.println("********************************************");
			System.out.println("Unbalanced AVL tree after insertion !!!");
			System.out.println("********************************************");
			System.out.println("Tree before rebalance:\n");
			printBST();
			System.out.println("-------------------------------------------");

			tri_node_restructure( x, y, z );

			System.out.println("Tree after rebalance:\n");
			printBST();
			System.out.println("********************************************");
		}
	}

	/* =======================================================
		tri_node_restructure(x, y, z):

		x = parent(y)
		y = parent(z)
	======================================================= */
	public void tri_node_restructure( Node x, Node y, Node z)
	{
		/* *******************************************************************
			Determine the parent child relationships between (y,z) and (x,y))
			******************************************************************* */
		boolean zIsLeftChild = (z == y.left);
		boolean yIsLeftChild = (y == x.left);

		/* =======================================================
		Determine the configuration:

		   find out which nodes are in positions a, b and c
		   given in the following legend:

				        b
				      /   \
				     a     c
		======================================================= */
		Node a, b, c;
		Node T0, T1, T2, T3;

		if (zIsLeftChild && yIsLeftChild) 
		{ /* Configuration 1 */
			System.out.println("Use tri-node restructuring op #1");

			a = z;                     //          x=c
			b = y;                     //         /  \
			c = x;                               //       y=b  T3 
			T0 = z.left;              //      /  \ 
			T1 = z.right;            //    z=a  T2
			T2 = y.right;            //   /  \
			T3 = x.right;                  //  T0  T1
		}
		else if (!zIsLeftChild && yIsLeftChild) 
		{ /* Configuration 2 */
			System.out.println("Use tri-node restructuring op #2");

			a = y;                     //       x=c
			b = z;                     //      /  \
			c = x;                               //    y=a  T3
			T0 = y.left;             //   /    \
			T1 = z.left;                    //  T0   z=b
			T2 = z.right;          //  /  \ 
			T3 = x.right;         // T1  T2
		}
		else if (zIsLeftChild && !yIsLeftChild) 
		{ /* Configuration 4 */
			System.out.println("Use tri-node restructuring op #4");

			a = x;                     //      x=a
			b = z;                     //     /  \
			c = y;                              //    T0  y=c
			T0 = x.left;               //       /  \ 
			T1 = z.left;               //      z=b  T3
			T2 = z.right;              //     /  \  
			T3 = y.right;              //    T1  T2 
		}
		else 
		{ /* Configuration 3 */
			System.out.println("Use tri-node restructuring op #3");

			a = x;                      //       x=a
			b = y;                      //      /   \
			c = z;                                //     T0   y=b
			T0 = x.left;              //     	 /    \
			T1 = y.left;                     //         T1     z=c
			T2 = z.left;             //                  /   \
			T3 = z.right;                 //               T2     T3
		}
	      
		/* ------------------------------------------------------------------
			Put b at x's place (make b the root of the new subtree !)
			------------------------------------------------------------------ */
		if ( x == root )
		{  /* If x is the root node, handle the replacement  differently.... */

			root = b;                   // b is now root
			b.parent = null;
		}
		else 
		{
			Node xParent;

			xParent = x.parent;   // Find x's parent

			if ( x == xParent.left ) 
			{ /* Link b to the left branch of x's parent */
				b.parent = xParent;
				xParent.left = b;
			}
			else 
			{ /* Link b to the right branch of x's parent */
				b.parent = xParent;
				xParent.right = b;
			}
		}
	 
		     /* ------------------
			Make:   b
			       / \
			      a   c
			------------------ */
		b.left = a;
		a.parent = b;
		b.right = c;
		c.parent = b;
	 
	 
		     /* ------------------
			Make:   b
			       / \
			      a   c
			     / \
			    T0 T1
			------------------ */	
			a.left = T0;
			if ( T0 != null ) 
				T0.parent = a;
			a.right = T1;
			if ( T1 != null ) 
				T1.parent = a;
		 
		     /* ------------------
			Make:   b
			       / \
			      a   c
				 / \
				T2 T3
			------------------ */
			c.left = T2;
			if ( T2 != null ) 
				T2.parent= c;
			c.right= T3;
			if ( T3 != null ) 
				T3.parent= c;
	 
			getHeight(a);
			getHeight(c);
	}

	/* =======================================================
		remove(k): delete node containg key k
	======================================================= */
	public void remove(String k)
	{
		Node p, q;     // Help variables
		Node parent;   // parent node
		Node succ;     // successor node

		/* --------------------------------------------
			Find the node with key == "key" in the BST
		-------------------------------------------- */
		p = findNode(k);

		if ( ! k.equals( p.key ) )
			return;			// Not found ==> nothing to delete....


		/* ========================================================
			Hibbard's Algorithm
		======================================================== */

		if ( p.left == null && p.right == null ) // Case 0: p has no children
		{
			parent = p.parent;

			/* --------------------------------
				Delete p from p's parent
			-------------------------------- */
			if ( parent.left == p )
				parent.left = null;
			else
				parent.right = null;

			/* --------------------------------------------
				Recompute the height of all parent nodes...
			-------------------------------------------- */
			getHeight( parent );

			return;
		}

	       if ( p.left == null )                 // Case 1a: p has 1 (right) child
	       {
			parent = p.parent;

			/* ----------------------------------------------
				Link p's right child as p's parent child
			---------------------------------------------- */
			if ( parent.left == p )
				parent.left = p.right;
			else
				parent.right = p.right;

			/* --------------------------------------------
				Recompute the height of all parent nodes...
			-------------------------------------------- */
			getHeight( parent );

			return;
		}

	       if ( p.right== null )                 // Case 1b: p has 1 (left) child
	       {
		  parent = p.parent;

		  /* ----------------------------------------------
		     Link p's left child as p's parent child
		     ---------------------------------------------- */
		  if ( parent.left == p )
		     parent.left = p.left;
		  else
		     parent.right = p.left;

		  /* --------------------------------------------
		     Recompute the height of all parent nodes...
		     -------------------------------------------- */
		  getHeight( parent );

		  return;
	       }

	       /* ================================================================
			Tough case: node has 2 children - find successor of p

			succ(p) is as as follows:  1 step right, all the way left

			Note: succ(p) has NOT left child !
		  ================================================================ */
		succ = p.right;			// p has 2 children....

		while ( succ.left != null )
			succ = succ.left;

		p.key = succ.key;		// Replace p with successor
		p.value = succ.value;


		/* --------------------------------
			Delete succ from succ's parent
		-------------------------------- */
		parent = succ.parent;

		if ( parent.left == succ )
			parent.left = succ.right;	// parent skip over succ ...
		else
			parent.right = succ.right;    // ... and point to succ's right child

		/* --------------------------------------------
			Recompute the height of all parent nodes...
		-------------------------------------------- */
		getHeight( parent );

		return;

	}	



	/* =======================================================
		Show what the BST look like....
	======================================================= */
	public void printnode(Node x, int h)
	{
		for (int i = 0; i < h; i++)
			System.out.print("               ");

		System.out.print("[" + x.key + "," + x.value + "](h=" + x.height + ")");

		if ( getDiffOfHeight( x.left, x.right) > 1 )
			System.out.println("*");
		else
			System.out.println();
	}

	void printBST()
	{
		showR( root, 0 );
		System.out.println("================================");
	}

	public void showR(Node t, int h)
	{
		if (t == null)
			return;

		showR(t.right, h+1);
		printnode(t, h);
		showR(t.left, h+1);
	}


	/* ================================================================
		getMaxHeight(t1,t2): compute max height of 2 (sub)trees
	================================================================ */
	public static int getMaxHeight( Node t1, Node t2 )
	{
		int h1, h2;

		if ( t1 == null )
			h1 = 0;
		else
			h1 = t1.height;

		if ( t2 == null )
			h2 = 0;
		else
			h2 = t2.height;	

		return (h1 >= h2) ? h1 : h2 ;
	}
	
	/* ================================================================
		getDiffOfHeight(t1,t2): compute difference in height of 2 (sub)trees
	================================================================ */
	public static int getDiffOfHeight( Node t1, Node t2 )
	{
		int h1, h2;

		if ( t1 == null )
			h1 = 0;
		else
			h1 = t1.height;

		if ( t2 == null )
			h2 = 0;
		else
			h2 = t2.height;

		return ((h1 >= h2) ? (h1-h2) : (h2-h1)) ;
	}

	/* ================================================================
		getHeight(x): recompute height starting at x (and up)
	================================================================ */
	public static void getHeight( Node x )
	{
		while ( x != null )
		{
			x.height = getMaxHeight( x.left, x.right ) + 1;
			x = x.parent;
		}
	}
}

public class Test_ADT_AVL
{
	public static void main(String[] args)
	{
		Scanner s = new Scanner(System.in);

		ADT_BY_AVL x = new ADT_BY_AVL();  

	
		System.out.println("How many operation you want to perform");
		int no_of_operation=s.nextInt();
		String ky;
		Integer vl;
	      
		System.out.println();
		System.out.println("Enter code as below:");
		System.out.println("1 :- insertion");
		System.out.println("2 :- search");
		System.out.println();
	      
		for(int i=0;i<no_of_operation;i++)
		{
			System.out.println("Enter code:");
			int code =s.nextInt();
			switch (code)
			{
				case 1:
					System.out.println("Insert the KEY");
					ky=s.next();
					System.out.println("Insert the value");
					vl=s.nextInt();
					x.put(ky,vl);
					break;
				case 2:
					System.out.println("Insert the KEY");
					ky=s.next();
					vl=x.get(ky);
					System.out.println("Key = " + ky + " ==> value: " + vl);
					break;
				default:
					System.out.println("Invalid sequence of input");
			}
			x.printBST();
			System.out.println();
			System.out.println();
		}
	}
}

