package l2j.gameserver.handler.community;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import l2j.gameserver.data.ClanData;
import l2j.gameserver.instancemanager.communitybbs.Community;
import l2j.gameserver.instancemanager.communitybbs.CommunityForumInstance;
import l2j.gameserver.instancemanager.communitybbs.CommunityPostInstance;
import l2j.gameserver.instancemanager.communitybbs.CommunityPostInstance.CPost;
import l2j.gameserver.instancemanager.communitybbs.CommunityTopicInstance;
import l2j.gameserver.instancemanager.communitybbs.CommunityTopicInstance.ConstructorType;
import l2j.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author fissban
 */
public class CommunityMemo extends AbstractCommunityHandler
{
	// Memo
	private static final List<CommunityTopicInstance> topics = new ArrayList<>();
	// Topic
	private static final Map<CommunityTopicInstance, CommunityPostInstance> postByTopic = new HashMap<>();
	
	@Override
	public String[] getCmdList()
	{
		return new String[]
		{
			"_bbsmemo"
		};
	}
	
	@Override
	public void useCommunityCommand(StringTokenizer st, L2PcInstance activeChar)
	{
		st.nextToken();// _bbsmemo
		
		if (!st.hasMoreTokens())
		{
			CommunityForumInstance memo = getCharacterMemo(activeChar);
			int memoId = memo.getId();
			showTopics(memo, activeChar, 1, memoId);
		}
		else
		{
			String command = st.nextToken();
			switch (command)
			{
				case "read":
					int read_idf = Integer.parseInt(st.nextToken());
					int ind_read = 1;
					if (st.hasMoreTokens())
					{
						ind_read = Integer.parseInt(st.nextToken());
					}
					showTopics(Community.getInstance().getForumByID(read_idf), activeChar, ind_read, read_idf);
					break;
				
				case "crea":
					int crea_idf = Integer.parseInt(st.nextToken());
					showNewTopic(Community.getInstance().getForumByID(crea_idf), activeChar, crea_idf);
					break;
				
				case "del":
					int del_idf = Integer.parseInt(st.nextToken());
					int del_idt = Integer.parseInt(st.nextToken());
					delTopic(Community.getInstance().getForumByID(del_idf), activeChar, del_idf, del_idt);
					break;
				
				case "readPost":
					int idf_readPost = Integer.parseInt(st.nextToken());
					int idp_readPost = Integer.parseInt(st.nextToken());
					String index = null;
					if (st.hasMoreTokens())
					{
						index = st.nextToken();
					}
					int ind_readPost = 0;
					if (index == null)
					{
						ind_readPost = 1;
					}
					else
					{
						ind_readPost = Integer.parseInt(index);
					}
					
					showPost((getTopicByID(idp_readPost)), Community.getInstance().getForumByID(idf_readPost), activeChar, ind_readPost);
					break;
				
				case "editPost":
					int idf_editPost = Integer.parseInt(st.nextToken());
					int idt_editPost = Integer.parseInt(st.nextToken());
					int idp_editPost = Integer.parseInt(st.nextToken());
					showEditPost((getTopicByID(idt_editPost)), Community.getInstance().getForumByID(idf_editPost), activeChar, idp_editPost);
					break;
				
				default:
					separateAndSend("<html><body><br><br><center>the command: " + command + " is not implemented yet</center><br><br></body></html>", activeChar);
					break;
				
			}
		}
	}
	
	@Override
	public String getWriteList()
	{
		return "Topic";
	}
	
	@Override
	public void useCommunityWrite(L2PcInstance activeChar, String ar1, String ar2, String ar3, String ar4, String ar5)
	{
		switch (ar1)
		{
			case "crea":
				CommunityForumInstance forum_crea = Community.getInstance().getForumByID(Integer.parseInt(ar2));
				if (forum_crea == null)
				{
					separateAndSend("<html><body><br><br><center>the forum: " + ar2 + " is not implemented yet</center><br><br></body></html>", activeChar);
				}
				else
				{
					forum_crea.vload();
					CommunityTopicInstance topic = new CommunityTopicInstance(ConstructorType.CREATE, Community.getInstance().getMaxId(forum_crea)
						+ 1, Integer.parseInt(ar2), ar5, Calendar.getInstance().getTimeInMillis(), activeChar.getName(), activeChar.getObjectId(), CommunityTopicInstance.MEMO, 0);
					addTopic(topic);
					forum_crea.addTopic(topic);
					Community.getInstance().setMaxId(topic.getID(), forum_crea);
					
					CommunityPostInstance post = new CommunityPostInstance(activeChar.getName(), activeChar.getObjectId(), Calendar.getInstance().getTimeInMillis(), topic.getID(), topic.getID(), ar4);
					addPostByTopic(post, topic);
					parseCmd("_bbsmemo", activeChar);
				}
				break;
			
			case "del":
				CommunityForumInstance forum_del = Community.getInstance().getForumByID(Integer.parseInt(ar2));
				if (forum_del == null)
				{
					separateAndSend("<html><body><br><br><center>the forum: " + ar2 + " is not implemented yet</center><br><br></body></html>", activeChar);
				}
				else
				{
					CommunityTopicInstance topic_del = forum_del.getTopic(Integer.parseInt(ar3));
					if (topic_del == null)
					{
						separateAndSend("<html><body><br><br><center>the forum: " + ar3 + " is not implemented yet</center><br><br></body></html>", activeChar);
					}
					else
					{
						// CPost cp = null;
						CommunityPostInstance post_del = getGPosttByTopic(topic_del);
						if (post_del != null)
						{
							post_del.deleteFromDB(topic_del);
							delPostByTopic(topic_del);
						}
						
						removeForumTopic(forum_del, topic_del);
						parseCmd("_bbsmemo", activeChar);
					}
				}
				break;
			case "post":
				StringTokenizer st = new StringTokenizer(ar1, " ");
				int idfPost = Integer.parseInt(st.nextToken());
				int idtPost = Integer.parseInt(st.nextToken());
				int idpPost = Integer.parseInt(st.nextToken());
				
				CommunityForumInstance forum = Community.getInstance().getForumByID(idfPost);
				if (forum == null)
				{
					separateAndSend("<html><body><br><br><center>the forum: " + idfPost + " does not exist !</center><br><br></body></html>", activeChar);
				}
				else
				{
					
					CommunityTopicInstance topic = forum.getTopic(idtPost);
					if (topic == null)
					{
						separateAndSend("<html><body><br><br><center>the topic: " + idtPost + " does not exist !</center><br><br></body></html>", activeChar);
					}
					else
					{
						CommunityPostInstance post = getGPosttByTopic(topic);
						if (post != null)
						{
							CPost cp = post.getCPost(idpPost);
							
							if (cp != null)
							{
								cp = post.getCPost(idpPost);
								post.getCPost(idpPost).postTxt = ar4;
								post.updatetxt(idpPost);
								parseCmd("_bbsposts read " + forum.getId() + " " + topic.getID(), activeChar);
							}
							else
							{
								separateAndSend("<html><body><br><br><center>the post: " + idpPost + " does not exist !</center><br><br></body></html>", activeChar);
							}
						}
					}
				}
				break;
			default:
				separateAndSend("<html><body><br><br><center>the command: " + ar1 + " is not implemented yet</center><br><br></body></html>", activeChar);
				break;
			
		}
	}
	
	/**
	 * Eliminamos un topic de la DB<br>
	 * Eliminamos un topic de su foro<br>
	 * Eliminamos un topic de nuestra lista<br>
	 * @param forum
	 * @param topic
	 */
	public void removeForumTopic(CommunityForumInstance forum, CommunityTopicInstance topic)
	{
		topic.deleteFromDB(forum);
		forum.rmTopicById(topic.getID());
		topics.remove(topic);
	}
	
	// METODOS VARIOS ------------------------------------------------------------------------------------------------------
	
	/** POST */
	
	/**
	 * @param topic
	 * @param forum
	 * @param activeChar
	 * @param idp
	 */
	private void showEditPost(CommunityTopicInstance topic, CommunityForumInstance forum, L2PcInstance activeChar, int idp)
	{
		CommunityPostInstance p = getGPosttByTopic(topic);
		if ((forum == null) || (topic == null) || (p == null))
		{
			separateAndSend("<html><body><br><br><center>Error, this forum, topic or post does not exit !</center><br><br></body></html>", activeChar);
		}
		else
		{
			showHtmlEditPost(topic, activeChar, forum, p);
		}
	}
	
	/**
	 * @param topic
	 * @param activeChar
	 * @param forum
	 * @param p
	 */
	private void showHtmlEditPost(CommunityTopicInstance topic, L2PcInstance activeChar, CommunityForumInstance forum, CommunityPostInstance p)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("<html><body><br><br>");
		sb.append("<table border=0 width=610><tr><td width=10></td><td width=600 align=left>");
		sb.append("<a action=\"bypass _bbsmemo\">HOME</a>&nbsp;>&nbsp;<a action=\"bypass _bbsmemo\">" + forum.getName() + " Form</a>");
		sb.append("</td></tr>");
		sb.append("</table>");
		sb.append("<img src=\"L2UI.squareblank\" width=\"1\" height=\"10\">");
		sb.append("<center>");
		sb.append("<table border=0 cellspacing=0 cellpadding=0>");
		sb.append("<tr><td width=610><img src=\"sek.cbui355\" width=\"610\" height=\"1\"><br1><img src=\"sek.cbui355\" width=\"610\" height=\"1\"></td></tr>");
		sb.append("</table>");
		sb.append("<table fixwidth=610 border=0 cellspacing=0 cellpadding=0>");
		sb.append("<tr><td><img src=\"l2ui.mini_logo\" width=5 height=20></td></tr>");
		sb.append("<tr>");
		sb.append("<td><img src=\"l2ui.mini_logo\" width=5 height=1></td>");
		sb.append("<td align=center FIXWIDTH=60 height=29>&$413;</td>");
		sb.append("<td FIXWIDTH=540>" + topic.getName() + "</td>");
		sb.append("<td><img src=\"l2ui.mini_logo\" width=5 height=1></td>");
		sb.append("</tr></table>");
		sb.append("<table fixwidth=610 border=0 cellspacing=0 cellpadding=0>");
		sb.append("<tr><td><img src=\"l2ui.mini_logo\" width=5 height=10></td></tr>");
		sb.append("<tr>");
		sb.append("<td><img src=\"l2ui.mini_logo\" width=5 height=1></td>");
		sb.append("<td align=center FIXWIDTH=60 height=29 valign=top>&$427;</td>");
		sb.append("<td align=center FIXWIDTH=540><MultiEdit var =\"Content\" width=535 height=313></td>");
		sb.append("<td><img src=\"l2ui.mini_logo\" width=5 height=1></td>");
		sb.append("</tr>");
		sb.append("<tr><td><img src=\"l2ui.mini_logo\" width=5 height=10></td></tr>");
		sb.append("</table>");
		sb.append("<table fixwidth=610 border=0 cellspacing=0 cellpadding=0>");
		sb.append("<tr><td><img src=\"l2ui.mini_logo\" width=5 height=10></td></tr>");
		sb.append("<tr>");
		sb.append("<td><img src=\"l2ui.mini_logo\" width=5 height=1></td>");
		sb.append("<td align=center FIXWIDTH=60 height=29>&nbsp;</td>");
		sb.append("<td align=center FIXWIDTH=70><button value=\"&$140;\" action=\"Write Topic post " + forum.getId() + " " + topic.getID() + " 0 _ Content Content Content\" back=\"l2ui_ch3.smallbutton2_down\" width=65 height=20 fore=\"l2ui_ch3.smallbutton2\" ></td>");
		sb.append("<td align=center FIXWIDTH=70><button value = \"&$141;\" action=\"bypass _bbsmemo\" back=\"l2ui_ch3.smallbutton2_down\" width=65 height=20 fore=\"l2ui_ch3.smallbutton2\"> </td>");
		sb.append("<td align=center FIXWIDTH=400>&nbsp;</td>");
		sb.append("<td><img src=\"l2ui.mini_logo\" width=5 height=1></td>");
		sb.append("</tr></table>");
		sb.append("</center>");
		sb.append("</body>");
		sb.append("</html>");
		send1001(sb.toString(), activeChar);
		send1002(activeChar, p.getCPost(0).postTxt, topic.getName(), DateFormat.getInstance().format(new Date(topic.getDate())));
	}
	
	/**
	 * @param topic
	 * @param forum
	 * @param activeChar
	 * @param ind
	 */
	private void showPost(CommunityTopicInstance topic, CommunityForumInstance forum, L2PcInstance activeChar, int ind)
	{
		if ((forum == null) || (topic == null))
		{
			separateAndSend("<html><body><br><br><center>Error, this forum is not implemented yet</center><br><br></body></html>", activeChar);
		}
		else if (forum.getType() == CommunityForumInstance.MEMO)
		{
			showMemoPost(topic, activeChar, forum);
		}
		else
		{
			separateAndSend("<html><body><br><br><center>the forum: " + forum.getName() + " is not implemented yet</center><br><br></body></html>", activeChar);
		}
	}
	
	/**
	 * @param topic
	 * @param activeChar
	 * @param forum
	 */
	private void showMemoPost(CommunityTopicInstance topic, L2PcInstance activeChar, CommunityForumInstance forum)
	{
		CommunityPostInstance p = getGPosttByTopic(topic);
		Locale locale = Locale.getDefault();
		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL, locale);
		StringBuilder sb = new StringBuilder("<html><body><br><br>");
		sb.append("<table border=0 width=610><tr><td width=10></td><td width=600 align=left>");
		sb.append("<a action=\"bypass _bbsmemo\">HOME</a>&nbsp;>&nbsp;<a action=\"bypass _bbsmemo\">Memo Form</a>");
		sb.append("</td></tr>");
		sb.append("</table>");
		sb.append("<img src=\"L2UI.squareblank\" width=\"1\" height=\"10\">");
		sb.append("<center>");
		sb.append("<table border=0 cellspacing=0 cellpadding=0 bgcolor=\"333333\">");
		sb.append("<tr><td height=10></td></tr>");
		sb.append("<tr>");
		sb.append("<td fixWIDTH=55 align=right valign=top>&$413; : &nbsp;</td>");
		sb.append("<td fixWIDTH=380 valign=top>" + topic.getName() + "</td>");
		sb.append("<td fixwidth=5></td>");
		sb.append("<td fixwidth=50></td>");
		sb.append("<td fixWIDTH=120></td>");
		sb.append("</tr>");
		sb.append("<tr><td height=10></td></tr>");
		sb.append("<tr>");
		sb.append("<td align=right><font color=\"AAAAAA\" >&$417; : &nbsp;</font></td>");
		sb.append("<td><font color=\"AAAAAA\">" + topic.getOwnerName() + "</font></td>");
		sb.append("<td></td>");
		sb.append("<td><font color=\"AAAAAA\">&$418; :</font></td>");
		sb.append("<td><font color=\"AAAAAA\">" + dateFormat.format(p.getCPost(0).postDate) + "</font></td>");
		sb.append("</tr>");
		sb.append("<tr><td height=10></td></tr>");
		sb.append("</table>");
		sb.append("<br>");
		sb.append("<table border=0 cellspacing=0 cellpadding=0>");
		sb.append("<tr>");
		sb.append("<td fixwidth=5></td>");
		String Mes = p.getCPost(0).postTxt.replace(">", "&gt;");
		Mes = Mes.replace("<", "&lt;");
		Mes = Mes.replace("\n", "<br1>");
		sb.append("<td FIXWIDTH=600 align=left>" + Mes + "</td>");
		sb.append("<td fixqqwidth=5></td>");
		sb.append("</tr>");
		sb.append("</table>");
		sb.append("<br>");
		sb.append("<img src=\"L2UI.squareblank\" width=\"1\" height=\"5\">");
		sb.append("<img src=\"L2UI.squaregray\" width=\"610\" height=\"1\">");
		sb.append("<img src=\"L2UI.squareblank\" width=\"1\" height=\"5\">");
		sb.append("<table border=0 cellspacing=0 cellpadding=0 FIXWIDTH=610>");
		sb.append("<tr>");
		sb.append("<td width=50>");
		sb.append("<button value=\"&$422;\" action=\"bypass _bbsmemo\" back=\"l2ui_ch3.smallbutton2_down\" width=65 height=20 fore=\"l2ui_ch3.smallbutton2\">");
		sb.append("</td>");
		sb.append("<td width=560 align=right><table border=0 cellspacing=0><tr>");
		sb.append("<td FIXWIDTH=300></td><td><button value = \"&$424;\" action=\"bypass _bbsmemo;editPost;" + forum.getId() + ";" + topic.getID() + ";0\" back=\"l2ui_ch3.smallbutton2_down\" width=65 height=20 fore=\"l2ui_ch3.smallbutton2\" ></td>&nbsp;");
		sb.append("<td><button value = \"&$425;\" action=\"bypass _bbsmemo;del;" + forum.getId() + ";" + topic.getID() + "\" back=\"l2ui_ch3.smallbutton2_down\" width=65 height=20 fore=\"l2ui_ch3.smallbutton2\" ></td>&nbsp;");
		sb.append("<td><button value = \"&$421;\" action=\"bypass _bbsmemo;crea;" + forum.getId() + "\" back=\"l2ui_ch3.smallbutton2_down\" width=65 height=20 fore=\"l2ui_ch3.smallbutton2\" ></td>&nbsp;");
		sb.append("</tr></table>");
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("</table>");
		sb.append("<br>");
		sb.append("<br>");
		sb.append("<br></center>");
		sb.append("</body>");
		sb.append("</html>");
		separateAndSend(sb.toString(), activeChar);
	}
	
	/** TOPICS */
	
	/**
	 * @param forum
	 * @param activeChar
	 * @param idf
	 * @param idt
	 */
	private void delTopic(CommunityForumInstance forum, L2PcInstance activeChar, int idf, int idt)
	{
		if (forum == null)
		{
			separateAndSend("<html><body><br><br><center>the forum: " + idf + " is not implemented yet</center><br><br></body></html>", activeChar);
		}
		else
		{
			CommunityTopicInstance topic = forum.getTopic(idt);
			if (topic == null)
			{
				separateAndSend("<html><body><br><br><center>the topic: " + idt + " does not exist !</center><br><br></body></html>", activeChar);
			}
			else
			{
				// CPost cp = null;
				CommunityPostInstance post = getGPosttByTopic(topic);
				if (post != null)
				{
					post.deleteFromDB(topic);
					delPostByTopic(topic);
				}
				removeForumTopic(forum, topic);
				parseCmd("_bbsmemo", activeChar);
			}
		}
	}
	
	/**
	 * @param forum
	 * @param activeChar
	 * @param idf
	 */
	private void showNewTopic(CommunityForumInstance forum, L2PcInstance activeChar, int idf)
	{
		if (forum == null)
		{
			separateAndSend("<html><body><br><br><center>the forum: " + idf + " is not implemented yet</center><br><br></body></html>", activeChar);
		}
		else if (forum.getType() == CommunityForumInstance.MEMO)
		{
			showMemoNewTopics(forum, activeChar);
		}
		else
		{
			separateAndSend("<html><body><br><br><center>the forum: " + forum.getName() + " is not implemented yet</center><br><br></body></html>", activeChar);
		}
	}
	
	/**
	 * @param forum
	 * @param activeChar
	 */
	private void showMemoNewTopics(CommunityForumInstance forum, L2PcInstance activeChar)
	{
		StringBuilder sb = new StringBuilder("<html>");
		sb.append("<body><br><br>");
		sb.append("<table border=0 width=610><tr><td width=10></td><td width=600 align=left>");
		sb.append("<a action=\"bypass _bbsmemo\">HOME</a>&nbsp;>&nbsp;<a action=\"bypass _bbsmemo\">Memo Form</a>");
		sb.append("</td></tr>");
		sb.append("</table>");
		sb.append("<img src=\"L2UI.squareblank\" width=\"1\" height=\"10\">");
		sb.append("<center>");
		sb.append("<table border=0 cellspacing=0 cellpadding=0>");
		sb.append("<tr><td width=610><img src=\"sek.cbui355\" width=\"610\" height=\"1\"><br1><img src=\"sek.cbui355\" width=\"610\" height=\"1\"></td></tr>");
		sb.append("</table>");
		sb.append("<table fixwidth=610 border=0 cellspacing=0 cellpadding=0>");
		sb.append("<tr><td><img src=\"l2ui.mini_logo\" width=5 height=20></td></tr>");
		sb.append("<tr>");
		sb.append("<td><img src=\"l2ui.mini_logo\" width=5 height=1></td>");
		sb.append("<td align=center FIXWIDTH=60 height=29>&$413;</td>");
		sb.append("<td FIXWIDTH=540><edit var = \"Title\" width=540 height=13></td>");
		sb.append("<td><img src=\"l2ui.mini_logo\" width=5 height=1></td>");
		sb.append("</tr></table>");
		sb.append("<table fixwidth=610 border=0 cellspacing=0 cellpadding=0>");
		sb.append("<tr><td><img src=\"l2ui.mini_logo\" width=5 height=10></td></tr>");
		sb.append("<tr>");
		sb.append("<td><img src=\"l2ui.mini_logo\" width=5 height=1></td>");
		sb.append("<td align=center FIXWIDTH=60 height=29 valign=top>&$427;</td>");
		sb.append("<td align=center FIXWIDTH=540><MultiEdit var =\"Content\" width=535 height=313></td>");
		sb.append("<td><img src=\"l2ui.mini_logo\" width=5 height=1></td>");
		sb.append("</tr>");
		sb.append("<tr><td><img src=\"l2ui.mini_logo\" width=5 height=10></td></tr>");
		sb.append("</table>");
		sb.append("<table fixwidth=610 border=0 cellspacing=0 cellpadding=0>");
		sb.append("<tr><td><img src=\"l2ui.mini_logo\" width=5 height=10></td></tr>");
		sb.append("<tr>");
		sb.append("<td><img src=\"l2ui.mini_logo\" width=5 height=1></td>");
		sb.append("<td align=center FIXWIDTH=60 height=29>&nbsp;</td>");
		sb.append("<td align=center FIXWIDTH=70><button value=\"&$140;\" action=\"Write Topic crea " + forum.getId() + " Title Content Title\" back=\"l2ui_ch3.smallbutton2_down\" width=65 height=20 fore=\"l2ui_ch3.smallbutton2\" ></td>");
		sb.append("<td align=center FIXWIDTH=70><button value = \"&$141;\" action=\"bypass _bbsmemo\" back=\"l2ui_ch3.smallbutton2_down\" width=65 height=20 fore=\"l2ui_ch3.smallbutton2\"> </td>");
		sb.append("<td align=center FIXWIDTH=400>&nbsp;</td>");
		sb.append("<td><img src=\"l2ui.mini_logo\" width=5 height=1></td>");
		sb.append("</tr></table>");
		sb.append("</center>");
		sb.append("</body>");
		sb.append("</html>");
		send1001(sb.toString(), activeChar);
		send1002(activeChar);
	}
	
	/**
	 * @param forum
	 * @param activeChar
	 * @param index
	 * @param idf
	 */
	private void showTopics(CommunityForumInstance forum, L2PcInstance activeChar, int index, int idf)
	{
		if (forum == null)
		{
			separateAndSend("<html><body><br><br><center>the forum: is not implemented yet</center><br><br></body></html>", activeChar);
		}
		else if (forum.getType() == CommunityForumInstance.MEMO)
		{
			showMemoTopics(forum, activeChar, index);
		}
		else
		{
			separateAndSend("<html><body><br><br><center>the forum: " + forum.getName() + " is not implemented yet</center><br><br></body></html>", activeChar);
		}
	}
	
	/** MEMO */
	
	/**
	 * @param forum
	 * @param activeChar
	 * @param index
	 */
	private void showMemoTopics(CommunityForumInstance forum, L2PcInstance activeChar, int index)
	{
		forum.vload();
		StringBuilder sb = new StringBuilder("<html><body><br><br>");
		sb.append("<table border=0 width=610><tr><td width=10></td><td width=600 align=left>");
		sb.append("<a action=\"bypass _bbsmemo\">HOME</a>&nbsp;>&nbsp;<a action=\"bypass _bbsmemo\">Memo Form</a>");
		sb.append("</td></tr>");
		sb.append("</table>");
		sb.append("<img src=\"L2UI.squareblank\" width=\"1\" height=\"10\">");
		sb.append("<center>");
		sb.append("<table border=0 cellspacing=0 cellpadding=2 bgcolor=888888 width=610>");
		sb.append("<tr>");
		sb.append("<td FIXWIDTH=5></td>");
		sb.append("<td FIXWIDTH=415 align=center>&$413;</td>");
		sb.append("<td FIXWIDTH=120 align=center></td>");
		sb.append("<td FIXWIDTH=70 align=center>&$418;</td>");
		sb.append("</tr>");
		sb.append("</table>");
		
		for (int i = 0, j = Community.getInstance().getMaxId(forum) + 1; i < (12 * index); j--)
		{
			if (j < 0)
			{
				break;
			}
			CommunityTopicInstance t = forum.getTopic(j);
			if (t != null)
			{
				if (i >= (12 * (index - 1)))
				{
					sb.append("<table border=0 cellspacing=0 cellpadding=5 WIDTH=610>");
					sb.append("<tr>");
					sb.append("<td FIXWIDTH=5></td>");
					sb.append("<td FIXWIDTH=415><a action=\"bypass _bbsmemo;readPost;" + forum.getId() + ";" + t.getID() + "\">" + t.getName() + "</a></td>");
					sb.append("<td FIXWIDTH=120 align=center></td>");
					sb.append("<td FIXWIDTH=70 align=center>" + DateFormat.getInstance().format(new Date(t.getDate())) + "</td>");
					sb.append("</tr>");
					sb.append("</table>");
					sb.append("<img src=\"L2UI.Squaregray\" width=\"610\" height=\"1\">");
				}
				i++;
			}
		}
		
		sb.append("<br>");
		sb.append("<table width=610 cellspace=0 cellpadding=0>");
		sb.append("<tr>");
		sb.append("<td width=50>");
		sb.append("<button value=\"&$422;\" action=\"bypass _bbsmemo\" back=\"l2ui_ch3.smallbutton2_down\" width=65 height=20 fore=\"l2ui_ch3.smallbutton2\">");
		sb.append("</td>");
		sb.append("<td width=510 align=center>");
		sb.append("<table border=0><tr>");
		
		if (index == 1)
		{
			sb.append("<td><button action=\"\" back=\"l2ui_ch3.prev1_down\" fore=\"l2ui_ch3.prev1\" width=16 height=16 ></td>");
		}
		else
		{
			sb.append("<td><button action=\"bypass _bbsmemo;topic;" + forum.getId() + ";" + (index - 1) + "\" back=\"l2ui_ch3.prev1_down\" fore=\"l2ui_ch3.prev1\" width=16 height=16 ></td>");
		}
		int nbp;
		nbp = forum.getTopicSize() / 8;
		if ((nbp * 8) != ClanData.getInstance().getClans().size())
		{
			nbp++;
		}
		for (int i = 1; i <= nbp; i++)
		{
			if (i == index)
			{
				sb.append("<td> " + i + " </td>");
			}
			else
			{
				sb.append("<td><a action=\"bypass _bbsmemo;topic;" + forum.getId() + ";" + i + "\"> " + i + " </a></td>");
			}
		}
		if (index == nbp)
		{
			sb.append("<td><button action=\"\" back=\"l2ui_ch3.next1_down\" fore=\"l2ui_ch3.next1\" width=16 height=16 ></td>");
		}
		else
		{
			sb.append("<td><button action=\"bypass _bbsmemo;topic;" + forum.getId() + ";" + (index + 1) + "\" back=\"l2ui_ch3.next1_down\" fore=\"l2ui_ch3.next1\" width=16 height=16 ></td>");
		}
		
		sb.append("</tr></table> </td> ");
		sb.append("<td align=right><button value = \"&$421;\" action=\"bypass _bbsmemo;crea;" + forum.getId() + "\" back=\"l2ui_ch3.smallbutton2_down\" width=65 height=20 fore=\"l2ui_ch3.smallbutton2\" ></td></tr>");
		sb.append("<tr><td><img src=\"l2ui.mini_logo\" width=5 height=10></td></tr>");
		sb.append("<tr> ");
		sb.append("<td></td>");
		sb.append("<td align=center><table border=0><tr><td></td><td><edit var = \"Search\" width=130 height=11></td>");
		sb.append("<td><button value=\"&$420;\" action=\"Write 5 -2 0 Search _ _\" back=\"l2ui_ch3.smallbutton2_down\" width=65 height=20 fore=\"l2ui_ch3.smallbutton2\"> </td> </tr></table> </td>");
		sb.append("</tr>");
		sb.append("</table>");
		sb.append("<br>");
		sb.append("<br>");
		sb.append("<br>");
		sb.append("</center>");
		sb.append("</body>");
		sb.append("</html>");
		separateAndSend(sb.toString(), activeChar);
	}
	
	// MANEJO DE LISTAS ----------------------------------------------------------------------------------------------------
	
	public void clearPost()
	{
		postByTopic.clear();
	}
	
	public CommunityPostInstance getGPosttByTopic(CommunityTopicInstance t)
	{
		CommunityPostInstance post = postByTopic.get(t);
		if (post == null)
		{
			postByTopic.put(t, new CommunityPostInstance(t));
		}
		return post;
	}
	
	/**
	 * @param tt
	 */
	public void addTopic(CommunityTopicInstance tt)
	{
		topics.add(tt);
	}
	
	/**
	 * @param t
	 */
	public void delPostByTopic(CommunityTopicInstance t)
	{
		postByTopic.remove(t);
	}
	
	public void addPostByTopic(CommunityPostInstance p, CommunityTopicInstance t)
	{
		if (postByTopic.get(t) == null)
		{
			postByTopic.put(t, p);
		}
	}
	
	/**
	 * Borramos el topic de nuestra lista
	 * @param topic
	 */
	public void delTopic(CommunityTopicInstance topic)
	{
		topics.remove(topic);
	}
	
	/**
	 * @param  idf
	 * @return
	 */
	public CommunityTopicInstance getTopicByID(int idf)
	{
		for (CommunityTopicInstance t : topics)
		{
			if (t.getID() == idf)
			{
				return t;
			}
		}
		return null;
	}
	
	// SACADO DE L2PCINSTANCE ----------------------------------------------------------------------------------------------
	
	// Variable para llevar el control de los players
	private static final Map<String, CommunityForumInstance> _forumMail = new HashMap<>();
	private static final Map<String, CommunityForumInstance> _forumMemo = new HashMap<>();
	
	public synchronized CommunityForumInstance getCharacterMail(L2PcInstance activeChar)
	{
		if (!_forumMail.containsKey(activeChar.getAccountName()))
		{
			_forumMail.put(activeChar.getAccountName(), Community.getInstance().getForumByName("MailRoot").getChildByName(activeChar.getName()));
			
			if (_forumMail.get(activeChar.getAccountName()) == null)
			{
				Community.getInstance().createNewForum(activeChar.getName(), Community.getInstance().getForumByName("MailRoot"), CommunityForumInstance.MAIL, CommunityForumInstance.OWNERONLY, activeChar.getObjectId());
				Community.getInstance().getForumByName("MailRoot").getChildByName(activeChar.getName());
			}
		}
		
		return _forumMail.get(activeChar.getAccountName());
	}
	
	public synchronized CommunityForumInstance getCharacterMemo(L2PcInstance activeChar)
	{
		if (!_forumMemo.containsKey(activeChar.getAccountName()))
		{
			_forumMemo.put(activeChar.getAccountName(), Community.getInstance().getForumByName("MemoRoot").getChildByName(activeChar.getAccountName()));
			
			if (_forumMemo.get(activeChar.getAccountName()) == null)
			{
				Community.getInstance().createNewForum(activeChar.getAccountName(), Community.getInstance().getForumByName("MemoRoot"), CommunityForumInstance.MEMO, CommunityForumInstance.OWNERONLY, activeChar.getObjectId());
				_forumMemo.put(activeChar.getAccountName(), Community.getInstance().getForumByName("MemoRoot").getChildByName(activeChar.getAccountName()));
			}
		}
		
		return _forumMemo.get(activeChar.getAccountName());
	}
}
