package com.example.util;

import com.example.model.ITopics;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by szjdj on 2016-11-02.
 */
public class HttpUtil {
   public static final String ProcessUrl = "http://120.76.22.150:8080/CellBank";

 /**
     * 登录注册
     */
    public static final String login = ProcessUrl+"/login.action";

    /*下拉刷新*/
    public static final String onRefreshurl = ProcessUrl+"/getTopics1.action";

    /*搜索*/
    public static final String searchUrl=ProcessUrl+"/searchByKey.action";

    /*评论*/
    public static  final  String TopicUrl=ProcessUrl+"/getComRep.action";

    /*罐体数据*/
    public static  final String StemCellUrl=ProcessUrl+"/getCellInfo.action";

    /*更改昵称，更改地址，更改email，更改密码都用这个*/
    public static  final String  ChangeUrl=ProcessUrl+"/UpdateInfo.action";

    public static final String detailUrl = ProcessUrl+"/detailInfo.action";

    public static final String addTopicUrl = ProcessUrl+"/AddTopic.action";

    public static final String addComUrl = ProcessUrl+"/addComment.action";
    public static final String addRepUrl = ProcessUrl+"/AddReply.action";

    public static  final String changepassword=ProcessUrl+"/changePw1.action";


//    //public static final String getPUrl = "http://192.168.1.116:8080/";
//    public static final String getPUrl = "http://192.168.1.150:8080/";
    /*滚动图片*/
    public static final String pgreplacement=ProcessUrl+"/replaceApp";

   /* *//*引导页图片替换*//*
    public static final String   guidancepage =getPUrl+"CellBank/guideReplaceApp";*/
    /*专家团队*/
    public static final String teamexperts=ProcessUrl+"/expertTeamApp";

   /* *//*产品介绍*//*
    public static final String productpresentation=getPUrl+"LifeBank/productIntroductionApp";*/

    /*行业新闻动态*/
    public static final String Industrynews=ProcessUrl+"/industryNewsApp";

   /*新闻滚动条*/
    public static final String marqueetext=ProcessUrl+"/theScrollBarApp";

    /*公开课*/
    public static final String openclass=ProcessUrl+"/publicClassApp";

    /*临床案例*/
    public static final String clinicalcase=ProcessUrl+"/clinicalCaseApp";

    /*健康小知识*/
    public static final String healthKnowledgeApp=ProcessUrl+"/healthKnowledgeApp";

    /**
     * 获取群组列表
     */
    public static final String  getGroupList=ProcessUrl+"/getGroupList";

    /**
     * 专家列表*/
    public static final String getExpertsList=ProcessUrl+"/getExperts";

    public static boolean isPublish=false;
    public static boolean isOpenImg=false;
    public static boolean isOpenPhoto = false;
    public static boolean isopenCame = false;
    public static boolean isTopic=false;

    public static List<ITopics> Top2ComList = new ArrayList<ITopics>();

}
