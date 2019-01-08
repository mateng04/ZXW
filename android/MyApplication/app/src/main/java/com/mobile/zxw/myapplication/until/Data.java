package com.mobile.zxw.myapplication.until;

import com.mobile.zxw.myapplication.bean.PopItem;

import java.util.List;

/**
 * 文件名：Data.java
 * 作用：初始化菜单栏中的数据
 * Created by Chitose on 2018/5/3.
 */

public class Data {
    public static void initFirstData(List<PopItem> itemList){
        //一级目录List
        itemList.add(new PopItem(1, 0, "全职"));
        itemList.add(new PopItem(2, 0, "兼职"));
    }

    public static void initFirstMallData(List<PopItem> itemList){
        //一级目录List
        itemList.add(new PopItem(1, 0, "商城"));
        itemList.add(new PopItem(2, 0, "微商"));
    }

    public static void initSecondData(List<PopItem> itemList){
        //二级目录List
        itemList.add(new PopItem(1, 0, "全部"));
        itemList.add(new PopItem(2, 0, "正常"));
        itemList.add(new PopItem(3, 0, "过期"));

    }

    public static void initThirdMallData(List<PopItem> itemList){
        itemList.add(new PopItem(1000,0,"不限"));
        itemList.add(new PopItem(1,0,"服装饰品"));
        itemList.add(new PopItem(2,0,"鞋帽箱包"));
        itemList.add(new PopItem(3,0,"化妆护理"));
        itemList.add(new PopItem(14,0,"针织品"));
        itemList.add(new PopItem(15,0,"床上用品"));
        itemList.add(new PopItem(4,0,"母婴玩具"));
        itemList.add(new PopItem(5,0,"通讯办公"));
        itemList.add(new PopItem(6,0,"电器数码"));
        itemList.add(new PopItem(7,0,"食品饮料"));
        itemList.add(new PopItem(8,0,"农产品"));
        itemList.add(new PopItem(9,0,"农用物资"));
        itemList.add(new PopItem(10,0,"家居建材"));
        itemList.add(new PopItem(11,0,"车品户外"));
        itemList.add(new PopItem(12,0,"畜牧"));
        itemList.add(new PopItem(13,0,"宠物"));
        itemList.add(new PopItem(1000,1,"不限"));
        itemList.add(new PopItem(16,1,"女装系列"));
        itemList.add(new PopItem(18,1,"男装系列"));
        itemList.add(new PopItem(19,1,"内衣系列"));
        itemList.add(new PopItem(20,1,"服饰配件"));
        itemList.add(new PopItem(21,1,"珠宝首饰"));
        itemList.add(new PopItem(22,1,"礼品"));
        itemList.add(new PopItem(25,1,"钟表"));
        itemList.add(new PopItem(26,1,"服装店用具"));
        itemList.add(new PopItem(123,1,"童装"));
        itemList.add(new PopItem(124,1,"亲子装"));
        itemList.add(new PopItem(1000,2,"不限"));
        itemList.add(new PopItem(17,2,"时尚女鞋"));
        itemList.add(new PopItem(23,2,"流行男鞋"));
        itemList.add(new PopItem(24,2,"潮流女包"));
        itemList.add(new PopItem(27,2,"精品男包"));
        itemList.add(new PopItem(28,2,"功能箱包"));
        itemList.add(new PopItem(29,2,"休闲鞋类"));
        itemList.add(new PopItem(30,2,"帽子"));
        itemList.add(new PopItem(1000,3,"不限"));
        itemList.add(new PopItem(31,3,"面部护肤"));
        itemList.add(new PopItem(32,3,"洗发护发"));
        itemList.add(new PopItem(33,3,"身体护肤"));
        itemList.add(new PopItem(34,3,"口腔护理"));
        itemList.add(new PopItem(35,3,"女性护理"));
        itemList.add(new PopItem(36,3,"香水彩妆"));
        itemList.add(new PopItem(1000,4,"不限"));
        itemList.add(new PopItem(37,4,"婴儿辅食"));
        itemList.add(new PopItem(38,4,"安全奶粉"));
        itemList.add(new PopItem(39,4,"尿裤湿巾"));
        itemList.add(new PopItem(40,4,"喂养用品"));
        itemList.add(new PopItem(41,4,"洗护用品"));
        itemList.add(new PopItem(42,4,"童车童床"));
        itemList.add(new PopItem(43,4,"安全座椅"));
        itemList.add(new PopItem(44,4,"童装童鞋"));
        itemList.add(new PopItem(45,4,"妈妈专区"));
        itemList.add(new PopItem(46,4,"玩具乐器"));
        itemList.add(new PopItem(1000,5,"不限"));
        itemList.add(new PopItem(47,5,"手机通讯"));
        itemList.add(new PopItem(48,5,"手机配件"));
        itemList.add(new PopItem(49,5,"外设产品"));
        itemList.add(new PopItem(50,5,"学习用品"));
        itemList.add(new PopItem(51,5,"办公用品"));
        itemList.add(new PopItem(52,5,"网络产品"));
        itemList.add(new PopItem(1000,6,"不限"));
        itemList.add(new PopItem(53,6,"大家电"));
        itemList.add(new PopItem(54,6,"生活电器"));
        itemList.add(new PopItem(55,6,"厨房电器"));
        itemList.add(new PopItem(56,6,"健康护理"));
        itemList.add(new PopItem(57,6,"电脑整机"));
        itemList.add(new PopItem(58,6,"电脑配件"));
        itemList.add(new PopItem(59,6,"摄影摄像"));
        itemList.add(new PopItem(60,6,"数码配件"));
        itemList.add(new PopItem(61,6,"时尚影音"));
        itemList.add(new PopItem(62,6,"智能设备"));
        itemList.add(new PopItem(63,6,"电子教育"));
        itemList.add(new PopItem(1000,7,"不限"));
        itemList.add(new PopItem(64,7,"地方特产"));
        itemList.add(new PopItem(65,7,"干货调味"));
        itemList.add(new PopItem(66,7,"酒类"));
        itemList.add(new PopItem(67,7,"休闲食品"));
        itemList.add(new PopItem(68,7,"饮料冲调"));
        itemList.add(new PopItem(69,7,"茗茶"));
        itemList.add(new PopItem(70,7,"生鲜食品"));
        itemList.add(new PopItem(71,7,"进口食品"));
        itemList.add(new PopItem(1000,8,"不限"));
        itemList.add(new PopItem(72,8,"地方特产"));
        itemList.add(new PopItem(73,8,"粮油"));
        itemList.add(new PopItem(74,8,"干货"));
        itemList.add(new PopItem(75,8,"蔬菜"));
        itemList.add(new PopItem(76,8,"水果"));
        itemList.add(new PopItem(77,8,"副食"));
        itemList.add(new PopItem(78,8,"鸡蛋"));
        itemList.add(new PopItem(79,8,"其他"));
        itemList.add(new PopItem(112,8,"猪肉"));
        itemList.add(new PopItem(113,8,"牛肉"));
        itemList.add(new PopItem(114,8,"羊肉"));
        itemList.add(new PopItem(115,8,"鸡肉"));
        itemList.add(new PopItem(116,8,"驴肉"));
        itemList.add(new PopItem(117,8,"鹿肉"));
        itemList.add(new PopItem(118,8,"野猪肉"));
        itemList.add(new PopItem(119,8,"兔肉"));
        itemList.add(new PopItem(1000,9,"不限"));
        itemList.add(new PopItem(80,9,"农业化肥"));
        itemList.add(new PopItem(81,9,"种子种苗"));
        itemList.add(new PopItem(82,9,"农用工具"));
        itemList.add(new PopItem(83,9,"饲料"));
        itemList.add(new PopItem(84,9,"农机"));
        itemList.add(new PopItem(85,9,"农药"));
        itemList.add(new PopItem(1000,10,"不限"));
        itemList.add(new PopItem(86,10,"餐饮厨具"));
        itemList.add(new PopItem(87,10,"居家日用"));
        itemList.add(new PopItem(88,10,"收纳整理"));
        itemList.add(new PopItem(89,10,"家具灯具"));
        itemList.add(new PopItem(90,10,"洗护清洁"));
        itemList.add(new PopItem(91,10,"家装建材"));
        itemList.add(new PopItem(92,10,"净化除味"));
        itemList.add(new PopItem(93,10,"家装软饰"));
        itemList.add(new PopItem(94,10,"家纺"));
        itemList.add(new PopItem(95,10,"五金"));
        itemList.add(new PopItem(1000,11,"不限"));
        itemList.add(new PopItem(96,11,"汽车"));
        itemList.add(new PopItem(97,11,"电动车"));
        itemList.add(new PopItem(98,11,"其他"));
        itemList.add(new PopItem(99,11,"汽车配件"));
        itemList.add(new PopItem(100,11,"运动健身"));
        itemList.add(new PopItem(1000,12,"不限"));
        itemList.add(new PopItem(101,12,"肉猪"));
        itemList.add(new PopItem(102,12,"种猪"));
        itemList.add(new PopItem(103,12,"母猪"));
        itemList.add(new PopItem(104,12,"山羊"));
        itemList.add(new PopItem(105,12,"绵羊"));
        itemList.add(new PopItem(106,12,"种羊"));
        itemList.add(new PopItem(107,12,"肉牛"));
        itemList.add(new PopItem(108,12,"奶牛"));
        itemList.add(new PopItem(109,12,"公鸡"));
        itemList.add(new PopItem(110,12,"蛋鸡"));
        itemList.add(new PopItem(111,12,"其它"));
        itemList.add(new PopItem(1000,13,"不限"));
        itemList.add(new PopItem(120,13,"猫"));
        itemList.add(new PopItem(121,13,"狗"));
        itemList.add(new PopItem(122,13,"兔子"));
        itemList.add(new PopItem(1000,14,"不限"));
        itemList.add(new PopItem(1000,15,"不限"));
        itemList.add(new PopItem(1000,1000,"不限"));
    }

    public static void initThirdData(List<PopItem> itemList){
        //不限的ID 是专用的，二级ID需要减1000
        itemList.add(new PopItem(10001,10000,"不限"));
        itemList.add(new PopItem(10000,0,"不限"));
        itemList.add(new PopItem(2,0,"司机"));
        itemList.add(new PopItem(17,0,"技工类"));
        itemList.add(new PopItem(37,0,"普工"));
        itemList.add(new PopItem(41,0,"修理工"));
        itemList.add(new PopItem(43,0,"操作工"));
        itemList.add(new PopItem(45,0,"厨师"));
        itemList.add(new PopItem(42,0,"服务员保安"));
        itemList.add(new PopItem(44,0,"教练员"));
        itemList.add(new PopItem(5,0,"财务类"));
        itemList.add(new PopItem(1,0,"生活服务"));
        itemList.add(new PopItem(7,0,"文职类"));
        itemList.add(new PopItem(6,0,"行政/人事类"));
        itemList.add(new PopItem(21,0,"轻工类"));
        itemList.add(new PopItem(14,0,"化工类"));
        itemList.add(new PopItem(16,0,"工业/工厂类"));
        itemList.add(new PopItem(8,0,"广告/装潢/工业设计类"));
        itemList.add(new PopItem(4,0,"企业经营/管理类"));
        itemList.add(new PopItem(32,0,"通讯/电子/电力"));
        itemList.add(new PopItem(28,0,"计算机/互联网类"));
        itemList.add(new PopItem(3,0,"市场/销售/业务类"));
        itemList.add(new PopItem(9,0,"房地产/建筑类"));
        itemList.add(new PopItem(10,0,"翻译类"));
        itemList.add(new PopItem(11,0,"制药类"));
        itemList.add(new PopItem(12,0,"运输/物流类"));
        itemList.add(new PopItem(13,0,"电力/能源/电气类"));
        itemList.add(new PopItem(15,0,"机械/设备类"));
        itemList.add(new PopItem(18,0,"商业/旅游/服务业类"));
        itemList.add(new PopItem(19,0,"金融/经济类"));
        itemList.add(new PopItem(20,0,"文化/教育/新闻/出版类"));
        itemList.add(new PopItem(22,0,"医疗/护理/保健类"));
        itemList.add(new PopItem(23,0,"律师/法务类"));
        itemList.add(new PopItem(25,0,"科学研究类"));
        itemList.add(new PopItem(24,0,"地矿冶金/测绘技术类"));
        itemList.add(new PopItem(26,0,"航空航天/核工业类"));
        itemList.add(new PopItem(27,0,"农林牧渔类"));
        itemList.add(new PopItem(35,0,"其它类别"));
        itemList.add(new PopItem(39,0,"其它"));
        itemList.add(new PopItem(10000,1,"不限"));
        itemList.add(new PopItem(1,1,"保姆"));
        itemList.add(new PopItem(2,1,"月嫂"));
        itemList.add(new PopItem(3,1,"钟点工"));
        itemList.add(new PopItem(4,1,"陪护"));
        itemList.add(new PopItem(5,1,"网站信息采编"));
        itemList.add(new PopItem(6,1,"网页设计/网页美工"));
        itemList.add(new PopItem(7,1,"Internet开发工程师"));
        itemList.add(new PopItem(8,1,"网站策划师"));
        itemList.add(new PopItem(9,1,"数据库工程师"));
        itemList.add(new PopItem(10,1,"多媒体设计与开发"));
        itemList.add(new PopItem(11,1,"技术经理/主管"));
        itemList.add(new PopItem(12,1,"项目经理"));
        itemList.add(new PopItem(13,1,"系统集成/技术支持"));
        itemList.add(new PopItem(14,1,"网站运营管理"));
        itemList.add(new PopItem(15,1,"系统安全工程师"));
        itemList.add(new PopItem(16,1,"网络管理/硬件维护"));
        itemList.add(new PopItem(17,1,"计算机软件/硬件销售"));
        itemList.add(new PopItem(18,1,"电子商务"));
        itemList.add(new PopItem(19,1,"网络工程师"));
        itemList.add(new PopItem(10000,2,"不限"));
        itemList.add(new PopItem(389,2,"半挂侧翻"));
        itemList.add(new PopItem(390,2,"半挂中翻"));
        itemList.add(new PopItem(391,2,"前四后八侧翻"));
        itemList.add(new PopItem(407,2,"大货车"));
        itemList.add(new PopItem(413,2,"学徒"));
        itemList.add(new PopItem(415,2,"水车"));
        itemList.add(new PopItem(424,2,"危运"));
        itemList.add(new PopItem(430,2,"其他"));
        itemList.add(new PopItem(433,2,"拖板车"));
        itemList.add(new PopItem(20,2,"半挂车"));
        itemList.add(new PopItem(21,2,"带挂车"));
        itemList.add(new PopItem(22,2,"前四后八"));
        itemList.add(new PopItem(23,2,"混凝土罐车"));
        itemList.add(new PopItem(24,2,"挖掘机"));
        itemList.add(new PopItem(25,2,"装载机"));
        itemList.add(new PopItem(26,2,"混凝土泵车"));
        itemList.add(new PopItem(27,2,"叉车"));
        itemList.add(new PopItem(28,2,"平地机"));
        itemList.add(new PopItem(29,2,"推土机"));
        itemList.add(new PopItem(30,2,"吊车"));
        itemList.add(new PopItem(31,2,"塔吊"));
        itemList.add(new PopItem(32,2,"出租车"));
        itemList.add(new PopItem(33,2,"小货车"));
        itemList.add(new PopItem(34,2,"三桥翻斗车"));
        itemList.add(new PopItem(35,2,"工程宽体车"));
        itemList.add(new PopItem(36,2,"拖拉机"));
        itemList.add(new PopItem(37,2,"收割机"));
        itemList.add(new PopItem(38,2,"耕地机"));
        itemList.add(new PopItem(420,2,"中巴"));
        itemList.add(new PopItem(421,2,"大巴"));
        itemList.add(new PopItem(422,2,"卧铺"));
        itemList.add(new PopItem(423,2,"公交车"));
        itemList.add(new PopItem(425,2,"压路机"));
        itemList.add(new PopItem(10000,3,"不限"));
        itemList.add(new PopItem(39,3,"销售经理/主管"));
        itemList.add(new PopItem(40,3,"商务/贸易/国际业务"));
        itemList.add(new PopItem(41,3,"销售工程师"));
        itemList.add(new PopItem(42,3,"业务员/业务代表/销售代表"));
        itemList.add(new PopItem(43,3,"报关"));
        itemList.add(new PopItem(44,3,"推销员/促销员"));
        itemList.add(new PopItem(45,3,"市场经理/主管"));
        itemList.add(new PopItem(46,3,"市场/行销策划"));
        itemList.add(new PopItem(47,3,"市场/行销策划"));
        itemList.add(new PopItem(48,3,"公关/礼仪"));
        itemList.add(new PopItem(49,3,"售前/售后技术服务"));
        itemList.add(new PopItem(50,3,"区域销售经理"));
        itemList.add(new PopItem(51,3,"快速消费品业务员"));
        itemList.add(new PopItem(52,3,"保险业务"));
        itemList.add(new PopItem(10000,4,"不限"));
        itemList.add(new PopItem(53,4,"总裁/总经理"));
        itemList.add(new PopItem(54,4,"企业策划"));
        itemList.add(new PopItem(55,4,"投资管理"));
        itemList.add(new PopItem(56,4,"管理顾问"));
        itemList.add(new PopItem(57,4,"企业管理人员"));
        itemList.add(new PopItem(58,4,"质管部经理"));
        itemList.add(new PopItem(59,4,"质管人员"));
        itemList.add(new PopItem(60,4,"项目经理/技术经理/CTO"));
        itemList.add(new PopItem(61,4,"经理助理"));
        itemList.add(new PopItem(62,4,"部门经理/主管"));
        itemList.add(new PopItem(63,4,"物料主管"));
        itemList.add(new PopItem(64,4,"采购主管"));
        itemList.add(new PopItem(65,4,"分公司/办事处经理/主管"));
        itemList.add(new PopItem(66,4,"物流管理"));
        itemList.add(new PopItem(10000,5,"不限"));
        itemList.add(new PopItem(67,5,"财务总监/经理"));
        itemList.add(new PopItem(68,5,"注册会计师/会计师"));
        itemList.add(new PopItem(69,5,"会计"));
        itemList.add(new PopItem(70,5,"出纳"));
        itemList.add(new PopItem(71,5,"统计"));
        itemList.add(new PopItem(72,5,"审计人员"));
        itemList.add(new PopItem(73,5,"税务经理/主管"));
        itemList.add(new PopItem(74,5,"成本管理"));
        itemList.add(new PopItem(75,5,"注册审计师"));
        itemList.add(new PopItem(76,5,"财务分析"));
        itemList.add(new PopItem(10000,6,"不限"));
        itemList.add(new PopItem(77,6,"行政经理/办公室主任"));
        itemList.add(new PopItem(78,6,"行政/人事人员"));
        itemList.add(new PopItem(79,6,"员工培训人员"));
        itemList.add(new PopItem(80,6,"企业文化/工会"));
        itemList.add(new PopItem(81,6,"人力资源经理/主管"));
        itemList.add(new PopItem(82,6,"培训经理"));
        itemList.add(new PopItem(10000,7,"不限"));
        itemList.add(new PopItem(83,7,"文职其他"));
        itemList.add(new PopItem(84,7,"图书情报/资料管理"));
        itemList.add(new PopItem(85,7,"技术资料编写"));
        itemList.add(new PopItem(86,7,"助理"));
        itemList.add(new PopItem(87,7,"文秘/高级文员"));
        itemList.add(new PopItem(88,7,"文员"));
        itemList.add(new PopItem(89,7,"前台接待"));
        itemList.add(new PopItem(90,7,"公关人员"));
        itemList.add(new PopItem(91,7,"公关主管"));
        itemList.add(new PopItem(92,7,"电脑打字员/操作员"));
        itemList.add(new PopItem(93,7,"文案策划"));
        itemList.add(new PopItem(94,7,"话务员"));
        itemList.add(new PopItem(95,7,"客户服务"));
        itemList.add(new PopItem(10000,8,"不限"));
        itemList.add(new PopItem(96,8,"美工"));
        itemList.add(new PopItem(97,8,"广告设计/创意策划"));
        itemList.add(new PopItem(98,8,"文案策划"));
        itemList.add(new PopItem(99,8,"工业设计/产品设计"));
        itemList.add(new PopItem(100,8,"多媒体设计与制作"));
        itemList.add(new PopItem(101,8,"装潢设计"));
        itemList.add(new PopItem(102,8,"工艺品设计"));
        itemList.add(new PopItem(103,8,"纺织服装设计"));
        itemList.add(new PopItem(104,8,"家具/珠宝设计"));
        itemList.add(new PopItem(105,8,"电脑制图人员/效果图设计"));
        itemList.add(new PopItem(106,8,"平面/3D设计"));
        itemList.add(new PopItem(107,8,"产品包装设计"));
        itemList.add(new PopItem(108,8,"排版设计"));
        itemList.add(new PopItem(109,8,"广告业务员"));
        itemList.add(new PopItem(10000,9,"不限"));
        itemList.add(new PopItem(110,9,"房地产开发/策划"));
        itemList.add(new PopItem(111,9,"建筑/结构工程师"));
        itemList.add(new PopItem(112,9,"工程监理"));
        itemList.add(new PopItem(113,9,"工程预决算/造价工程师"));
        itemList.add(new PopItem(114,9,"给排水/水电工程专业人员"));
        itemList.add(new PopItem(115,9,"制冷暖通"));
        itemList.add(new PopItem(116,9,"物业管理"));
        itemList.add(new PopItem(117,9,"室内(外)装修设计"));
        itemList.add(new PopItem(118,9,"室内(外)装修监理"));
        itemList.add(new PopItem(119,9,"建筑设计师"));
        itemList.add(new PopItem(120,9,"项目工程师"));
        itemList.add(new PopItem(121,9,"建筑制图"));
        itemList.add(new PopItem(122,9,"基建/岩土工程"));
        itemList.add(new PopItem(123,9,"园艺工程/园林技术/绿化工程"));
        itemList.add(new PopItem(124,9,"路桥技术/隧道工程/管道"));
        itemList.add(new PopItem(125,9,"城市规划/市政工程"));
        itemList.add(new PopItem(126,9,"建筑材料"));
        itemList.add(new PopItem(127,9,"工长"));
        itemList.add(new PopItem(128,9,"工民建"));
        itemList.add(new PopItem(129,9,"资料员"));
        itemList.add(new PopItem(130,9,"工程测绘"));
        itemList.add(new PopItem(131,9,"楼盘销售"));
        itemList.add(new PopItem(132,9,"房地产专业人员"));
        itemList.add(new PopItem(10000,10,"不限"));
        itemList.add(new PopItem(140,10,"其它外语翻译"));
        itemList.add(new PopItem(133,10,"英语翻译"));
        itemList.add(new PopItem(134,10,"朝鲜语翻译"));
        itemList.add(new PopItem(135,10,"日语翻译"));
        itemList.add(new PopItem(136,10,"德语翻译"));
        itemList.add(new PopItem(137,10,"法语翻译"));
        itemList.add(new PopItem(138,10,"俄语翻译"));
        itemList.add(new PopItem(139,10,"西班牙语翻译"));
        itemList.add(new PopItem(10000,11,"不限"));
        itemList.add(new PopItem(141,11,"生物制药技术"));
        itemList.add(new PopItem(142,11,"医药销售代表"));
        itemList.add(new PopItem(143,11,"药物合成"));
        itemList.add(new PopItem(144,11,"药物研发/临床开发"));
        itemList.add(new PopItem(145,11,"化学药剂/药品"));
        itemList.add(new PopItem(146,11,"医疗器械销售"));
        itemList.add(new PopItem(147,11,"药品报批/药品注册"));
        itemList.add(new PopItem(148,11,"QA/QC/质量管理"));
        itemList.add(new PopItem(149,11,"药品生产制造"));
        itemList.add(new PopItem(10000,12,"不限"));
        itemList.add(new PopItem(150,12,"公路运输"));
        itemList.add(new PopItem(151,12,"铁道运输"));
        itemList.add(new PopItem(152,12,"民航运输"));
        itemList.add(new PopItem(153,12,"船舶运输"));
        itemList.add(new PopItem(154,12,"调度员"));
        itemList.add(new PopItem(155,12,"压运员"));
        itemList.add(new PopItem(156,12,"船务人员"));
        itemList.add(new PopItem(157,12,"报关员/单证员"));
        itemList.add(new PopItem(158,12,"物流管理"));
        itemList.add(new PopItem(159,12,"物流操作员"));
        itemList.add(new PopItem(160,12,"仓储管理"));
        itemList.add(new PopItem(161,12,"店面管理"));
        itemList.add(new PopItem(162,12,"快递员"));
        itemList.add(new PopItem(10000,13,"不限"));
        itemList.add(new PopItem(163,13,"电力生产"));
        itemList.add(new PopItem(164,13,"火力发电"));
        itemList.add(new PopItem(165,13,"水力发电"));
        itemList.add(new PopItem(166,13,"核力发电"));
        itemList.add(new PopItem(167,13,"电力供应"));
        itemList.add(new PopItem(168,13,"石油/天然气"));
        itemList.add(new PopItem(169,13,"电力维修/电力拖动"));
        itemList.add(new PopItem(170,13,"发配电工程/电力管理"));
        itemList.add(new PopItem(171,13,"内燃机及热能动力"));
        itemList.add(new PopItem(10000,14,"不限"));
        itemList.add(new PopItem(172,14,"化工其他"));
        itemList.add(new PopItem(173,14,"造纸/废品处理"));
        itemList.add(new PopItem(174,14,"玻璃/硅酸盐工业"));
        itemList.add(new PopItem(175,14,"农药、化肥"));
        itemList.add(new PopItem(176,14,"无机化工"));
        itemList.add(new PopItem(177,14,"有机化工"));
        itemList.add(new PopItem(178,14,"高分子化工/化纤/新材料"));
        itemList.add(new PopItem(179,14,"精细/日用化工"));
        itemList.add(new PopItem(180,14,"化工产品销售"));
        itemList.add(new PopItem(181,14,"塑料/橡胶工业"));
        itemList.add(new PopItem(182,14,"生物化工"));
        itemList.add(new PopItem(183,14,"电化工"));
        itemList.add(new PopItem(10000,15,"不限"));
        itemList.add(new PopItem(184,15,"机械工程师"));
        itemList.add(new PopItem(185,15,"模具/模型工程师"));
        itemList.add(new PopItem(186,15,"机电一体化工程师"));
        itemList.add(new PopItem(187,15,"各种车辆/飞行器设计"));
        itemList.add(new PopItem(188,15,"设备主管"));
        itemList.add(new PopItem(189,15,"机械设计工程师"));
        itemList.add(new PopItem(190,15,"机械制图员"));
        itemList.add(new PopItem(191,15,"机械维修"));
        itemList.add(new PopItem(192,15,"机械产品销售"));
        itemList.add(new PopItem(193,15,"机械加工技术"));
        itemList.add(new PopItem(194,15,"焊接/切割技术"));
        itemList.add(new PopItem(195,15,"铸造/锻造"));
        itemList.add(new PopItem(196,15,"注塑成型"));
        itemList.add(new PopItem(197,15,"气动/液压"));
        itemList.add(new PopItem(198,15,"锅炉/压力容器"));
        itemList.add(new PopItem(199,15,"仪器仪表"));
        itemList.add(new PopItem(200,15,"内燃机"));
        itemList.add(new PopItem(201,15,"汽车/摩托车工程师"));
        itemList.add(new PopItem(10000,16,"不限"));
        itemList.add(new PopItem(429,16,"生产工"));
        itemList.add(new PopItem(202,16,"厂长/副厂长"));
        itemList.add(new PopItem(203,16,"生产管理"));
        itemList.add(new PopItem(204,16,"工程管理"));
        itemList.add(new PopItem(205,16,"质量/品质管理"));
        itemList.add(new PopItem(206,16,"物料管理"));
        itemList.add(new PopItem(207,16,"设备管理"));
        itemList.add(new PopItem(208,16,"采购管理"));
        itemList.add(new PopItem(209,16,"仓库管理"));
        itemList.add(new PopItem(210,16,"计划员/调度员"));
        itemList.add(new PopItem(211,16,"化验/检验员"));
        itemList.add(new PopItem(212,16,"产品开发"));
        itemList.add(new PopItem(213,16,"安全管理"));
        itemList.add(new PopItem(214,16,"ISO专员"));
        itemList.add(new PopItem(10000,17,"不限"));
        itemList.add(new PopItem(419,17,"混凝土泵工"));
        itemList.add(new PopItem(427,17,"其它"));
        itemList.add(new PopItem(215,17,"钳工/机修工/钣金工"));
        itemList.add(new PopItem(216,17,"电焊工/铆焊工"));
        itemList.add(new PopItem(217,17,"车工/磨工/铣工"));
        itemList.add(new PopItem(218,17,"模具/模型工"));
        itemList.add(new PopItem(219,17,"印刷工"));
        itemList.add(new PopItem(220,17,"水工/木工/油漆工"));
        itemList.add(new PopItem(221,17,"电工"));
        itemList.add(new PopItem(222,17,"空调工/电梯工/锅炉工"));
        itemList.add(new PopItem(223,17,"裁剪车缝熨烫"));
        itemList.add(new PopItem(224,17,"冲床/镗床/刨床"));
        itemList.add(new PopItem(10000,18,"不限"));
        itemList.add(new PopItem(228,18,"司机"));
        itemList.add(new PopItem(229,18,"保安"));
        itemList.add(new PopItem(230,18,"服务员"));
        itemList.add(new PopItem(231,18,"营业员"));
        itemList.add(new PopItem(232,18,"商场管理人员"));
        itemList.add(new PopItem(233,18,"大堂/前厅经理"));
        itemList.add(new PopItem(234,18,"客房部经理"));
        itemList.add(new PopItem(235,18,"领班"));
        itemList.add(new PopItem(236,18,"音效师"));
        itemList.add(new PopItem(237,18,"餐饮经理"));
        itemList.add(new PopItem(238,18,"厨师"));
        itemList.add(new PopItem(239,18,"理货员"));
        itemList.add(new PopItem(240,18,"健身教练"));
        itemList.add(new PopItem(241,18,"接线员"));
        itemList.add(new PopItem(242,18,"美容/美发师"));
        itemList.add(new PopItem(243,18,"导游"));
        itemList.add(new PopItem(244,18,"酒店管理人员"));
        itemList.add(new PopItem(245,18,"其他"));
        itemList.add(new PopItem(246,18,"客户服务/投诉处理"));
        itemList.add(new PopItem(247,18,"收银员"));
        itemList.add(new PopItem(248,18,"防损员"));
        itemList.add(new PopItem(249,18,"娱乐管理"));
        itemList.add(new PopItem(250,18,"演艺人员/模特"));
        itemList.add(new PopItem(10000,19,"不限"));
        itemList.add(new PopItem(251,19,"金融/投资"));
        itemList.add(new PopItem(252,19,"证券期货"));
        itemList.add(new PopItem(253,19,"保险业"));
        itemList.add(new PopItem(254,19,"税务人员"));
        itemList.add(new PopItem(255,19,"其它金融/经济人员"));
        itemList.add(new PopItem(256,19,"拍卖师"));
        itemList.add(new PopItem(257,19,"资产评估"));
        itemList.add(new PopItem(258,19,"外汇管理人员"));
        itemList.add(new PopItem(259,19,"预结算人员"));
        itemList.add(new PopItem(260,19,"稽核员"));
        itemList.add(new PopItem(261,19,"信贷员"));
        itemList.add(new PopItem(10000,20,"不限"));
        itemList.add(new PopItem(262,20,"文化其他"));
        itemList.add(new PopItem(263,20,"广播电视/文化艺术"));
        itemList.add(new PopItem(264,20,"高等教育"));
        itemList.add(new PopItem(265,20,"中等教育"));
        itemList.add(new PopItem(266,20,"小学/幼儿教育"));
        itemList.add(new PopItem(267,20,"职业教育/培训"));
        itemList.add(new PopItem(268,20,"体育"));
        itemList.add(new PopItem(269,20,"总编"));
        itemList.add(new PopItem(270,20,"编辑"));
        itemList.add(new PopItem(271,20,"发行主管"));
        itemList.add(new PopItem(272,20,"记者"));
        itemList.add(new PopItem(273,20,"导演"));
        itemList.add(new PopItem(274,20,"摄影师"));
        itemList.add(new PopItem(275,20,"影视策划/制作人员"));
        itemList.add(new PopItem(276,20,"主持人"));
        itemList.add(new PopItem(277,20,"戏剧/舞蹈"));
        itemList.add(new PopItem(278,20,"教练员"));
        itemList.add(new PopItem(279,20,"教学/教务管理人员"));
        itemList.add(new PopItem(10000,21,"不限"));
        itemList.add(new PopItem(280,21,"纸浆造纸工艺"));
        itemList.add(new PopItem(281,21,"制鞋/制衣/制革"));
        itemList.add(new PopItem(282,21,"食品/糖酒/粮油"));
        itemList.add(new PopItem(283,21,"陶瓷技术"));
        itemList.add(new PopItem(284,21,"金银首饰加工"));
        itemList.add(new PopItem(285,21,"其他"));
        itemList.add(new PopItem(286,21,"服装纺织技术人员"));
        itemList.add(new PopItem(287,21,"印刷技术人员"));
        itemList.add(new PopItem(288,21,"包装技术人员"));
        itemList.add(new PopItem(10000,22,"不限"));
        itemList.add(new PopItem(305,22,"医学美容"));
        itemList.add(new PopItem(289,22,"中医师"));
        itemList.add(new PopItem(290,22,"西医师"));
        itemList.add(new PopItem(291,22,"医药技术员"));
        itemList.add(new PopItem(292,22,"护士/护理人员"));
        itemList.add(new PopItem(293,22,"医/药学检验员"));
        itemList.add(new PopItem(294,22,"兽医/宠物医生"));
        itemList.add(new PopItem(295,22,"心理医生"));
        itemList.add(new PopItem(296,22,"药剂师"));
        itemList.add(new PopItem(297,22,"麻醉师"));
        itemList.add(new PopItem(298,22,"卫生防疫"));
        itemList.add(new PopItem(299,22,"妇幼保健"));
        itemList.add(new PopItem(300,22,"针灸推拿"));
        itemList.add(new PopItem(301,22,"化妆/美容师"));
        itemList.add(new PopItem(302,22,"按摩师"));
        itemList.add(new PopItem(303,22,"其他"));
        itemList.add(new PopItem(304,22,"牙科医生"));
        itemList.add(new PopItem(10000,23,"不限"));
        itemList.add(new PopItem(306,23,"法务人员"));
        itemList.add(new PopItem(307,23,"律师助理"));
        itemList.add(new PopItem(308,23,"书记员"));
        itemList.add(new PopItem(309,23,"法律顾问"));
        itemList.add(new PopItem(310,23,"律师"));
        itemList.add(new PopItem(10000,24,"不限"));
        itemList.add(new PopItem(311,24,"勘查"));
        itemList.add(new PopItem(312,24,"采矿/选矿"));
        itemList.add(new PopItem(313,24,"其他地质冶金"));
        itemList.add(new PopItem(314,24,"工程测量"));
        itemList.add(new PopItem(315,24,"大地/海洋测量"));
        itemList.add(new PopItem(316,24,"地图制图与印刷"));
        itemList.add(new PopItem(10000,25,"不限"));
        itemList.add(new PopItem(317,25,"自然科学研究"));
        itemList.add(new PopItem(318,25,"工程和技术研究"));
        itemList.add(new PopItem(319,25,"农业科学研究"));
        itemList.add(new PopItem(320,25,"医学研究"));
        itemList.add(new PopItem(321,25,"社会人文科学研究"));
        itemList.add(new PopItem(10000,26,"不限"));
        itemList.add(new PopItem(322,26,"航空"));
        itemList.add(new PopItem(323,26,"航天"));
        itemList.add(new PopItem(324,26,"其他航空航天"));
        itemList.add(new PopItem(325,26,"核工业"));
        itemList.add(new PopItem(10000,27,"不限"));
        itemList.add(new PopItem(326,27,"种植业"));
        itemList.add(new PopItem(327,27,"林业"));
        itemList.add(new PopItem(328,27,"畜牧/兽医"));
        itemList.add(new PopItem(329,27,"渔业"));
        itemList.add(new PopItem(330,27,"水利"));
        itemList.add(new PopItem(331,27,"农业推广"));
        itemList.add(new PopItem(332,27,"饲料"));
        itemList.add(new PopItem(10000,28,"不限"));
        itemList.add(new PopItem(363,28,"Internet"));
        itemList.add(new PopItem(364,28,"网站策划师"));
        itemList.add(new PopItem(365,28,"数据库工程师"));
        itemList.add(new PopItem(366,28,"多媒体设计与开发"));
        itemList.add(new PopItem(367,28,"技术经理/主管"));
        itemList.add(new PopItem(368,28,"项目经理"));
        itemList.add(new PopItem(369,28,"系统集成/技术支持"));
        itemList.add(new PopItem(370,28,"网站运营管理"));
        itemList.add(new PopItem(371,28,"系统安全工程师"));
        itemList.add(new PopItem(372,28,"网络管理/硬件维护"));
        itemList.add(new PopItem(373,28,"计算机软件/硬件"));
        itemList.add(new PopItem(374,28,"电子商务"));
        itemList.add(new PopItem(375,28,"网络工程师"));
        itemList.add(new PopItem(333,28,"系统测试"));
        itemList.add(new PopItem(334,28,"软件工程师"));
        itemList.add(new PopItem(335,28,"硬件工程师"));
        itemList.add(new PopItem(336,28,"系统工程师"));
        itemList.add(new PopItem(337,28,"网站信息采编"));
        itemList.add(new PopItem(338,28,"网页设计/网页美"));
        itemList.add(new PopItem(10000,32,"不限"));
        itemList.add(new PopItem(343,32,"电子工程师"));
        itemList.add(new PopItem(344,32,"电子元器件工程师"));
        itemList.add(new PopItem(345,32,"自动控制"));
        itemList.add(new PopItem(346,32,"智能大厦/综合布线"));
        itemList.add(new PopItem(347,32,"仪器仪表/计量"));
        itemList.add(new PopItem(348,32,"电气(器)/电力"));
        itemList.add(new PopItem(349,32,"通讯工程师/电信"));
        itemList.add(new PopItem(350,32,"单片机/DSP"));
        itemList.add(new PopItem(351,32,"射频/微波工程师"));
        itemList.add(new PopItem(352,32,"强电/弱电"));
        itemList.add(new PopItem(353,32,"电路/布线设计"));
        itemList.add(new PopItem(354,32,"无线电技术"));
        itemList.add(new PopItem(355,32,"半导体技术"));
        itemList.add(new PopItem(356,32,"通讯/电子/电力"));
        itemList.add(new PopItem(357,32,"电气维修"));
        itemList.add(new PopItem(358,32,"变压器/磁电工程"));
        itemList.add(new PopItem(359,32,"家用电器"));
        itemList.add(new PopItem(360,32,"光源与照明工程"));
        itemList.add(new PopItem(361,32,"广播电视工程"));
        itemList.add(new PopItem(10000,35,"不限"));
        itemList.add(new PopItem(377,35,"声光学技术"));
        itemList.add(new PopItem(378,35,"冶金/喷涂/金属"));
        itemList.add(new PopItem(379,35,"安全消防"));
        itemList.add(new PopItem(380,35,"激光技术"));
        itemList.add(new PopItem(381,35,"无/不限"));
        itemList.add(new PopItem(376,35,"其他"));
        itemList.add(new PopItem(10000,37,"不限"));
        itemList.add(new PopItem(384,37,"皮带工"));
        itemList.add(new PopItem(385,37,"清洁工"));
        itemList.add(new PopItem(386,37,"小工"));
        itemList.add(new PopItem(387,37,"门卫"));
        itemList.add(new PopItem(388,37,"其它"));
        itemList.add(new PopItem(418,37,"洗车工"));
        itemList.add(new PopItem(432,37,"洗煤工"));
        itemList.add(new PopItem(431,37,"快递员"));
        itemList.add(new PopItem(10000,41,"不限"));
        itemList.add(new PopItem(394,41,"宽体工程翻斗"));
        itemList.add(new PopItem(393,41,"半挂车"));
        itemList.add(new PopItem(395,41,"标体翻斗"));
        itemList.add(new PopItem(396,41,"装载机"));
        itemList.add(new PopItem(397,41,"挖掘机"));
        itemList.add(new PopItem(398,41,"小轿车"));
        itemList.add(new PopItem(399,41,"其它"));
        itemList.add(new PopItem(10000,42,"不限"));
        itemList.add(new PopItem(400,42,"饭店服务员"));
        itemList.add(new PopItem(401,42,"宾馆酒店"));
        itemList.add(new PopItem(402,42,"其它"));
        itemList.add(new PopItem(10000,43,"不限"));
        itemList.add(new PopItem(403,43,"钻井工"));
        itemList.add(new PopItem(416,43,"工厂操作工"));
        itemList.add(new PopItem(417,43,"机械操作工"));
        itemList.add(new PopItem(10000,44,"不限"));
        itemList.add(new PopItem(404,44,"驾校教练"));
        itemList.add(new PopItem(405,44,"武校教练"));
        itemList.add(new PopItem(406,44,"跆拳道教练"));
        itemList.add(new PopItem(10000,45,"不限"));
        itemList.add(new PopItem(408,45,"川菜"));
        itemList.add(new PopItem(409,45,"地方莱"));
        itemList.add(new PopItem(410,45,"粤菜"));
        itemList.add(new PopItem(411,45,"海鲜"));
        itemList.add(new PopItem(412,45,"烧烤"));
        itemList.add(new PopItem(414,45,"工地大师傅"));
        itemList.add(new PopItem(426,45,"烧烤"));
    }

}