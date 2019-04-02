//
// Created by 011102111 on 2019/4/1.
//
#include "SignCheck.h"
#include <string.h>
#include <stdio.h>

jboolean JNICALL Java_com_fei_firstproject_jniTest_SignCheck_isRight
  (JNIEnv *env, jclass jclazz, jobject contextObject) {

        /**
         * 发布的app 合法签名的签名字符串
         * signature[0].toCharString()得到
         *
         */
        const char* RELEASE_SIGN = "3082032f30820217a00302010202042790aeed300d06092a864886f70d01010b05003048310f300d06035504061306353138303030310b300906035504071302737a310c300a060355040a1303666569310c300a060355040b1303666569310c300a06035504031303466569301e170d3137303830383038323033345a170d3432303830323038323033345a3048310f300d06035504061306353138303030310b300906035504071302737a310c300a060355040a1303666569310c300a060355040b1303666569310c300a0603550403130346656930820122300d06092a864886f70d01010105000382010f003082010a02820101009e751865ecb0e2718f957fbff5ab78ad13dc7fe82077d01967158fc2114c4dfa418c0ace8ce254cb9f26098b414f42362eec694610069912aee9b96772a66c1ad6762fba020aad6877cb1c140c055bd45ccf736b3ee7a3699dd4c942408046bcb7d1fec1437283114cb95275684b8400c46a8277fba3f4ace8ef57bf76df3fbd34eced00746d8ed9f530bf8627aaabda20f7b46d1430f5215ef88e7d9257436f4c7ddff546b5d99115d1cd7fdd17ae64470634c234e8a9159cd64e559d9c00af663faf147d392b51562692f2f00a6ee845ad4082a7f8a962d613604436e973eb4c95693e34840b88760d36c4e130169025b0490ee65eaa209a5b2b02f24ccb330203010001a321301f301d0603551d0e04160414624d2034fab4b62cc852e651d991f1835f526667300d06092a864886f70d01010b0500038201010021c2430c24c358d8c5f3c90916dd655f77346b283b86d7efec362648fc914c1aef0cfa0e52c3cfc9dbd9449bb13f56d938e3e6ae2a722a5cf5074b17b67a544ead675e74998438f70e1ff472cf097a642930d24608bcc083b4d00eec4ee32b5fe06a4b08d3e6462acc11cd0c4b79e212b9b615067c141d05fabb1f03805d1629baaeaac715b5a9468fb41dc5fba0008f42945846520d3ef8d6af9e3500db19f76db7d5336b4a0e9ac965cda8668e0192b9b47246e7e40e1e2683ed45ab5a343f895c68449980e998885580cb0c7dad691d389fab8a710f538c47c22882c12906a60cd9c9904979c10125e6725d2d2cac2e6f0d722d94baf774f4d6a5bf295e56";

        jclass native_class = env->GetObjectClass(contextObject);
            jmethodID pm_id = env->GetMethodID(native_class, "getPackageManager", "()Landroid/content/pm/PackageManager;");
            jobject pm_obj = env->CallObjectMethod(contextObject, pm_id);
            jclass pm_clazz = env->GetObjectClass(pm_obj);
        // 得到 getPackageInfo 方法的 ID
            jmethodID package_info_id = env->GetMethodID(pm_clazz, "getPackageInfo","(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;");
            jclass native_classs = env->GetObjectClass(contextObject);
            jmethodID mId = env->GetMethodID(native_classs, "getPackageName", "()Ljava/lang/String;");
            jstring pkg_str = static_cast<jstring>(env->CallObjectMethod(contextObject, mId));
        // 获得应用包的信息
            jobject pi_obj = env->CallObjectMethod(pm_obj, package_info_id, pkg_str, 64);
        // 获得 PackageInfo 类
            jclass pi_clazz = env->GetObjectClass(pi_obj);
        // 获得签名数组属性的 ID
            jfieldID signatures_fieldId = env->GetFieldID(pi_clazz, "signatures", "[Landroid/content/pm/Signature;");
            jobject signatures_obj = env->GetObjectField(pi_obj, signatures_fieldId);
            jobjectArray signaturesArray = (jobjectArray)signatures_obj;
            jsize size = env->GetArrayLength(signaturesArray);
            jobject signature_obj = env->GetObjectArrayElement(signaturesArray, 0);
            jclass signature_clazz = env->GetObjectClass(signature_obj);

            //第一种方式--检查签名字符串的方式
            jmethodID string_id = env->GetMethodID(signature_clazz, "toCharsString", "()Ljava/lang/String;");
            jstring str = static_cast<jstring>(env->CallObjectMethod(signature_obj, string_id));
            char *c_msg = (char*)env->GetStringUTFChars(str,0);

            if(strcmp(c_msg,RELEASE_SIGN)==0)//签名一致  返回合法的 key，否则返回错误
            {
                return true;
            }else
            {
                return false;
            }
  }
