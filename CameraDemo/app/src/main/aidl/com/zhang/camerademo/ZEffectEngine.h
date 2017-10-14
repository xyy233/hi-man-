/*****************************************************************************
Copyright:    www.zealpixel.com(盗版必究)
File name:    ZPhotoFilterEngine.h
Description:  滤镜函数.
Author:       HZ
Version:      V1.0
Date:         2015-08-29
*****************************************************************************/

#ifndef __ZPHOTO_FILTER_ENGINE__
#define __ZPHOTO_FILTER_ENGINE__

const int FILTER_IDA = 0;
const int FILTER_IDB = 100;
const int FILTER_IDC = 200;
const int FILTER_IDD = 300;

//////////////////////////////////////////////////////////////////////////
//Filter width no mask
const int FILTER_IDA_NONE                                         = FILTER_IDA+0;
const int FILTER_IDA_1977                                         = FILTER_IDA+1;
const int FILTER_IDA_INKWELL                                      = FILTER_IDA+2;
const int FILTER_IDA_KELVIN                                       = FILTER_IDA+3;
const int FILTER_IDA_NASHVILLE                                    = FILTER_IDA+4;
const int FILTER_IDA_VALENCIA                                     = FILTER_IDA+5;
const int FILTER_IDA_XPROII                                       = FILTER_IDA+6;
const int FILTER_IDA_BRANNAN                                      = FILTER_IDA+7;
const int FILTER_IDA_WALDEN                                       = FILTER_IDA+8;
const int FILTER_IDA_ADEN                                         = FILTER_IDA+9;//add
const int FILTER_IDA_ASHBY                                        = FILTER_IDA+10;
const int FILTER_IDA_BROOKLYN                                     = FILTER_IDA+11;
const int FILTER_IDA_CHARMES                                      = FILTER_IDA+12;
const int FILTER_IDA_CLARENDON                                    = FILTER_IDA+13;
const int FILTER_IDA_CREMA                                        = FILTER_IDA+14;
const int FILTER_IDA_DOGPACH                                      = FILTER_IDA+15;
const int FILTER_IDA_GINGHAM                                      = FILTER_IDA+16;
const int FILTER_IDA_GINZA                                        = FILTER_IDA+17;
const int FILTER_IDA_HEFE                                         = FILTER_IDA+18;
const int FILTER_IDA_HELENA                                       = FILTER_IDA+19;
const int FILTER_IDA_JUNO                                         = FILTER_IDA+20;
const int FILTER_IDA_LARK                                         = FILTER_IDA+21;
const int FILTER_IDA_LUDWIG                                       = FILTER_IDA+22;
const int FILTER_IDA_MAVEN                                        = FILTER_IDA+23;
const int FILTER_IDA_MOON                                         = FILTER_IDA+24;
const int FILTER_IDA_REYES                                        = FILTER_IDA+25;
const int FILTER_IDA_SKYLINE                                      = FILTER_IDA+26;
const int FILTER_IDA_SLUMBER                                      = FILTER_IDA+27;
const int FILTER_IDA_STINSON                                      = FILTER_IDA+28;
const int FILTER_IDA_VESPER                                       = FILTER_IDA+29;


///////////////////////////////////////////////////////////////////////
const int FILTER_IDB_WARMER                                       = FILTER_IDB+0;//一键美颜_暖暖*
const int FILTER_IDB_CLEAR                                        = FILTER_IDB+1;//一键美颜_清晰
const int FILTER_IDB_WHITESKINNED                                 = FILTER_IDB+2;//一键美颜_白皙
const int FILTER_IDB_COOL                                         = FILTER_IDB+3;//一键美颜_冷艳
const int FILTER_IDB_ELEGANT                                      = FILTER_IDB+4;//LOMO_淡雅
const int FILTER_IDB_ANCIENT                                      = FILTER_IDB+5;//LOMO_复古
const int FILTER_IDB_GETE                                         = FILTER_IDB+6;//LOMO_哥特风
const int FILTER_IDB_BRONZE                                       = FILTER_IDB+7;//LOMO_古铜色
const int FILTER_IDB_LAKECOLOR                                    = FILTER_IDB+8;//LOMO_湖水*
const int FILTER_IDB_SLLY                                         = FILTER_IDB+9;//LOMO_深蓝泪雨*
const int FILTER_IDB_SLIVER                                       = FILTER_IDB+10;//格调_银色*
const int FILTER_IDB_FILM                                         = FILTER_IDB+11;//格调_胶片
const int FILTER_IDB_SUNNY                                        = FILTER_IDB+12;//格调_丽日
const int FILTER_IDB_WWOZ                                         = FILTER_IDB+13;//格调_绿野仙踪
const int FILTER_IDB_LOVERS                                       = FILTER_IDB+14;//格调_迷情*
const int FILTER_IDB_LATTE                                        = FILTER_IDB+15;//格调_拿铁
const int FILTER_IDB_JAPANESE                                     = FILTER_IDB+16;//格调_日系
const int FILTER_IDB_SANDGLASS                                    = FILTER_IDB+17;//格调_沙漏
const int FILTER_IDB_AFTEA                                        = FILTER_IDB+18;//格调_午茶??
const int FILTER_IDB_SHEEPSCROLL                                  = FILTER_IDB+19;//格调_羊皮卷
const int FILTER_IDB_PICNIC                                       = FILTER_IDB+20;//格调_野餐
const int FILTER_IDB_ICESPIRIT                                    = FILTER_IDB+21;//美颜_冰灵
const int FILTER_IDB_REFINED                                      = FILTER_IDB+22;//美颜_典雅
const int FILTER_IDB_BLUESTYLE                                    = FILTER_IDB+23;//美颜_蓝调
const int FILTER_IDB_LOLITA                                       = FILTER_IDB+24;//美颜_萝莉
const int FILTER_IDB_LKK                                          = FILTER_IDB+25;//美颜_洛可可
const int FILTER_IDB_NUANHUANG                                    = FILTER_IDB+26;//美颜_暖黄
const int FILTER_IDB_RCOOL                                        = FILTER_IDB+27;//美颜_清凉
const int FILTER_IDB_JSTYLE                                       = FILTER_IDB+28;//美颜_日系人像
const int FILTER_IDB_SOFTLIGHT                                    = FILTER_IDB+29;//美颜_柔光
const int FILTER_IDB_TIANMEI                                      = FILTER_IDB+30;//美颜_甜美可人
const int FILTER_IDB_WEIMEI                                       = FILTER_IDB+31;//美颜_唯美
const int FILTER_IDB_PURPLEDREAM                                  = FILTER_IDB+32;//美颜_紫色幻想
const int FILTER_IDB_FOOD                                         = FILTER_IDB+33;//智能_美食*

//////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////
const int FILTER_IDC_MOVIE                                        = FILTER_IDC+0; //LOMO_电影
const int FILTER_IDC_MAPLELEAF                                    = FILTER_IDC+1; //LOMO_枫叶
const int FILTER_IDC_COOLFLAME                                    = FILTER_IDC+2; //LOMO_冷焰
const int FILTER_IDC_WARMAUTUMN                                   = FILTER_IDC+3; //LOMO_暖秋
const int FILTER_IDC_CYAN                                         = FILTER_IDC+4; //LOMO_青色
const int FILTER_IDC_ZEAL                                         = FILTER_IDC+5; //LOMO_热情
const int FILTER_IDC_FASHION                                      = FILTER_IDC+6; //LOMO_时尚
const int FILTER_IDC_EKTAR                                        = FILTER_IDC+7; //弗莱胶片 -- ektar
const int FILTER_IDC_GOLD                                         = FILTER_IDC+8; //弗莱胶片 -- gold
const int FILTER_IDC_VISTA                                        = FILTER_IDC+9; //弗莱胶片 -- vista
const int FILTER_IDC_XTAR                                         = FILTER_IDC+10;//弗莱胶片 -- xtra
const int FILTER_IDC_RUDDY                                        = FILTER_IDC+11;//魔法美肤 -- 红润
const int FILTER_IDC_SUNSHINE                                     = FILTER_IDC+12;//魔法美肤 -- 暖暖阳光
const int FILTER_IDC_FRESH                                        = FILTER_IDC+13;//魔法美肤 -- 清新丽人
const int FILTER_IDC_SWEET                                        = FILTER_IDC+14;//魔法美肤 -- 甜美可人
const int FILTER_IDC_BLACKWHITE                                   = FILTER_IDC+15;//魔法美肤 -- 艺术黑白
const int FILTER_IDC_WHITENING                                    = FILTER_IDC+16;//魔法美肤 -- 自然美白
const int FILTER_IDC_JPELEGANT                                    = FILTER_IDC+17;//日系 -- 淡雅
const int FILTER_IDC_JPJELLY                                      = FILTER_IDC+18;//日系 -- 果冻
const int FILTER_IDC_JPFRESH                                      = FILTER_IDC+19;//日系 -- 清新
const int FILTER_IDC_JPSWEET                                      = FILTER_IDC+20;//日系 -- 甜美
const int FILTER_IDC_JPAESTHETICISM                               = FILTER_IDC+21;//日系 -- 唯美
const int FILTER_IDC_JPWARM                                       = FILTER_IDC+22;//日系 -- 温暖




//////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////
// filter effects

const int FILTER_IDD_CARTOON                                        = FILTER_IDD+0; //
const int FILTER_IDD_DARK                                           = FILTER_IDD+1; //暗调
const int FILTER_IDD_GLOW                                           = FILTER_IDD+2;
const int FILTER_IDD_LOMO                                           = FILTER_IDD+3;
const int FILTER_IDD_NEON                                           = FILTER_IDD+4; //霓虹
const int FILTER_IDD_OILPAINT                                       = FILTER_IDD+5; //油画
const int FILTER_IDD_PUNCH                                          = FILTER_IDD+6; //冲印
const int FILTER_IDD_REMINISCE                                      = FILTER_IDD+7; //怀旧
const int FILTER_IDD_SKETCH                                         = FILTER_IDD+8; //素描
const int FILTER_IDD_GRAPHIC                                        = FILTER_IDD+9; //连环画
const int FILTER_IDD_ABAOSE                                         = FILTER_IDD+10; //阿宝色



/////////////////////////////////////////////////////////////////////////
#ifdef _MSC_VER

#ifdef __cplusplus
#define EXPORT extern "C" _declspec(dllexport)
#else
#define EXPORT __declspec(dllexport)
#endif

EXPORT int ZPHOTO_Filter(unsigned char* srcData, int width, int height, int stride,int FilterId);
EXPORT int ZPHOTO_UniversalFilter(unsigned char* srcData, int width, int height, int stride, unsigned char* maskData, int mWidth, int mHeight, int mStride, int mergeMode, int ratio);
#else

#ifdef __cplusplus
extern "C" {
#endif    
	int ZPHOTO_Filter(unsigned char* srcData, int width, int height, int stride,int FilterId);
	int ZPHOTO_UniversalFilter(unsigned char* srcData, int width, int height, int stride, unsigned char* maskData, int mWidth, int mHeight, int mStride, int mergeMode, int ratio);
#ifdef __cplusplus
}
#endif


#endif

#endif
