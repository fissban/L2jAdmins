package l2j.mmocore;

public final class NioNetStackList<E>
{
	private final NioNetStackNode start = new NioNetStackNode();
	
	private final NioNetStackNodeBuf nodeBuf = new NioNetStackNodeBuf();
	
	private NioNetStackNode end = new NioNetStackNode();
	
	public NioNetStackList()
	{
		clear();
	}
	
	public final void addLast(final E elem)
	{
		var newEndNode = nodeBuf.removeFirst();
		end.value = elem;
		end.next = newEndNode;
		end = newEndNode;
	}
	
	public final E removeFirst()
	{
		var old = start.next;
		var value = old.value;
		start.next = old.next;
		nodeBuf.addLast(old);
		return value;
	}
	
	public final boolean isEmpty()
	{
		return start.next == end;
	}
	
	public final void clear()
	{
		start.next = end;
	}
	
	private final class NioNetStackNode
	{
		protected NioNetStackNode next;
		
		protected E value;
		
		protected NioNetStackNode()
		{
			
		}
	}
	
	private final class NioNetStackNodeBuf
	{
		private final NioNetStackNode startNode = new NioNetStackNode();
		
		private NioNetStackNode endNode = new NioNetStackNode();
		
		NioNetStackNodeBuf()
		{
			startNode.next = endNode;
		}
		
		final void addLast(final NioNetStackNode node)
		{
			node.next = null;
			node.value = null;
			endNode.next = node;
			endNode = node;
		}
		
		final NioNetStackNode removeFirst()
		{
			if (startNode.next == endNode)
			{
				return new NioNetStackNode();
			}
			
			var old = startNode.next;
			startNode.next = old.next;
			return old;
		}
	}
}