/*   1:    */ package com.alipay.api.beans;
/*   2:    */ 
/*   3:    */ import java.util.Locale;
/*   4:    */ import java.util.MissingResourceException;
/*   5:    */ import java.util.ResourceBundle;
/*   6:    */ 
/*   7:    */ public class Messages
/*   8:    */ {
/*   9: 50 */   private static ResourceBundle bundle = null;
/*  10:    */   
/*  11:    */   public static String getString(String msg)
/*  12:    */   {
/*  13: 60 */     if (bundle == null) {
/*  14: 61 */       return msg;
/*  15:    */     }
/*  16:    */     try
/*  17:    */     {
/*  18: 63 */       return bundle.getString(msg);
/*  19:    */     }
/*  20:    */     catch (MissingResourceException e) {}
/*  21: 65 */     return "Missing message: " + msg;
/*  22:    */   }
/*  23:    */   
/*  24:    */   public static String getString(String msg, Object arg)
/*  25:    */   {
/*  26: 79 */     return getString(msg, new Object[] { arg });
/*  27:    */   }
/*  28:    */   
/*  29:    */   public static String getString(String msg, int arg)
/*  30:    */   {
/*  31: 92 */     return getString(msg, new Object[] { Integer.toString(arg) });
/*  32:    */   }
/*  33:    */   
/*  34:    */   public static String getString(String msg, char arg)
/*  35:    */   {
/*  36:105 */     return getString(msg, new Object[] { String.valueOf(arg) });
/*  37:    */   }
/*  38:    */   
/*  39:    */   public static String getString(String msg, Object arg1, Object arg2)
/*  40:    */   {
/*  41:120 */     return getString(msg, new Object[] { arg1, arg2 });
/*  42:    */   }
/*  43:    */   
/*  44:    */   public static String getString(String msg, Object[] args)
/*  45:    */   {
/*  46:133 */     String format = msg;
/*  47:135 */     if (bundle != null) {
/*  48:    */       try
/*  49:    */       {
/*  50:137 */         format = bundle.getString(msg);
/*  51:    */       }
/*  52:    */       catch (MissingResourceException localMissingResourceException) {}
/*  53:    */     }
/*  54:142 */     return format(format, args);
/*  55:    */   }
/*  56:    */   
/*  57:    */   public static String format(String format, Object[] args)
/*  58:    */   {
/*  59:162 */     StringBuilder answer = new StringBuilder(format.length() + 
/*  60:163 */       args.length * 20);
/*  61:164 */     String[] argStrings = new String[args.length];
/*  62:165 */     for (int i = 0; i < args.length; i++) {
/*  63:166 */       if (args[i] == null) {
/*  64:167 */         argStrings[i] = "<null>";
/*  65:    */       } else {
/*  66:169 */         argStrings[i] = args[i].toString();
/*  67:    */       }
/*  68:    */     }
/*  69:171 */     int lastI = 0;
/*  70:172 */     for (int i = format.indexOf('{', 0); i >= 0; i = format.indexOf('{', 
/*  71:173 */           lastI)) {
/*  72:174 */       if ((i != 0) && (format.charAt(i - 1) == '\\'))
/*  73:    */       {
/*  74:176 */         if (i != 1) {
/*  75:177 */           answer.append(format.substring(lastI, i - 1));
/*  76:    */         }
/*  77:178 */         answer.append('{');
/*  78:179 */         lastI = i + 1;
/*  79:    */       }
/*  80:182 */       else if (i > format.length() - 3)
/*  81:    */       {
/*  82:184 */         answer.append(format.substring(lastI, format.length()));
/*  83:185 */         lastI = format.length();
/*  84:    */       }
/*  85:    */       else
/*  86:    */       {
/*  87:187 */         int argnum = (byte)Character.digit(format.charAt(i + 1), 
/*  88:188 */           10);
/*  89:189 */         if ((argnum < 0) || (format.charAt(i + 2) != '}'))
/*  90:    */         {
/*  91:191 */           answer.append(format.substring(lastI, i + 1));
/*  92:192 */           lastI = i + 1;
/*  93:    */         }
/*  94:    */         else
/*  95:    */         {
/*  96:195 */           answer.append(format.substring(lastI, i));
/*  97:196 */           if (argnum >= argStrings.length) {
/*  98:197 */             answer.append("<missing argument>");
/*  99:    */           } else {
/* 100:199 */             answer.append(argStrings[argnum]);
/* 101:    */           }
/* 102:200 */           lastI = i + 3;
/* 103:    */         }
/* 104:    */       }
/* 105:    */     }
/* 106:205 */     if (lastI < format.length()) {
/* 107:206 */       answer.append(format.substring(lastI, format.length()));
/* 108:    */     }
/* 109:207 */     return answer.toString();
/* 110:    */   }
/* 111:    */   
/* 112:    */   public static ResourceBundle setLocale(Locale locale, String resource)
/* 113:    */   {
/* 114:229 */     return null;
/* 115:    */   }
/* 116:    */   
/* 117:    */   static
/* 118:    */   {
/* 119:    */     try
/* 120:    */     {
/* 121:235 */       bundle = setLocale(Locale.getDefault(), 
/* 122:236 */         "org.apache.harmony.beans.internal.nls.messages");
/* 123:    */     }
/* 124:    */     catch (Throwable e)
/* 125:    */     {
/* 126:238 */       e.printStackTrace();
/* 127:    */     }
/* 128:    */   }
/* 129:    */ }



/* Location:           C:\Users\zhiya.zhang.C-STORE\Desktop\openbeans-1.0.jar

 * Qualified Name:     org.apache.harmony.beans.internal.nls.Messages

 * JD-Core Version:    0.7.0.1

 */