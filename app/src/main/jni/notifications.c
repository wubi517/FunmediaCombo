#include <jni.h>

//Links from MainActivity
JNIEXPORT jstring JNICALL Java_com_gold_kds517_funmedia_1new_MainActivity_getOne(JNIEnv *env, jobject instance){
    return (*env)->NewStringUTF(env, "aHR0cDovL3RoZXJhZGlvc2hhay5jby51ay9pcHR2L2luZGV4LnBocD9saWNlbmNlX2tleT1zc25pYw==");
}
JNIEXPORT jstring JNICALL Java_com_gold_kds517_funmedia_1new_MainActivity_getTwo(JNIEnv *env, jobject instance){
    return (*env)->NewStringUTF(env, "ZnVuc3Ri");
}
JNIEXPORT jstring JNICALL Java_com_gold_kds517_funmedia_1new_MainActivity_getThree(JNIEnv *env, jobject instance){
    return (*env)->NewStringUTF(env, "L3RoZXJhZGlvc2hhay5jTDNSb1pYSmhaR2x2YzJoaGF5NWphSFIwY0RvdkwzUm9aWEpoWkdsdmMyaGhheTVqYnk1MWF5OXBjSFIyWDNBeEwybHVaR1Y0TG5Cb2NEOXNhV05sYm1ObFgydGxlVDF6YzI1cE1RPT0=");
}
JNIEXPORT jstring JNICALL Java_com_gold_kds517_funmedia_1new_MainActivity_getFour(JNIEnv *env, jobject instance){
    return (*env)->NewStringUTF(env, "L3RoZXJhZGlvc2hhay5jTDNSb1pYSmhaR2x2YzJoaGF5NWphSFIwY0RvdkwzUm9aWEpoWkdsdmMyaGhheTVqYnk1MWF5OXBjSFIyWDNBeEwybHVaR1Y0TG5Cb2NEOXNhV05sYm1ObFgydGxlVDF6YzI1cE1RPT0=");
}
JNIEXPORT jstring JNICALL Java_com_gold_kds517_funmedia_1new_MainActivity_getFive(JNIEnv *env, jobject instance){
    return (*env)->NewStringUTF(env, "aHR0cDovL3RoZXJhZGlvc2hhay5jby51ay9pcHR2L2luZGV4LnBocD9saWNlbmNlX2tleT1zc25pMw==");
}
JNIEXPORT jstring JNICALL Java_com_gold_kds517_funmedia_1new_MainActivity_getSix(JNIEnv *env, jobject instance){
    return (*env)->NewStringUTF(env, "aHR0cDovL3RoZXJhZGlvc2hhay5jby51ay9zc25pYS9pY29uLnBuZw==");
}

JNIEXPORT jstring JNICALL Java_com_gold_kds517_funmedia_1new_MainActivity_getSeven(JNIEnv *env, jobject instance){
    return (*env)->NewStringUTF(env, "aHR0cDovL3RoZXJhZGlvc2hhay5jby51ay9zc25pYy9pY29uLnBuZw==");
}

JNIEXPORT jstring JNICALL Java_com_gold_kds517_funmedia_1new_MainActivity_getEight(JNIEnv *env, jobject instance){
    return (*env)->NewStringUTF(env, "aHR0cDovL3RoZXJhZGlvc2hhay5jby51ay9zc25pMy9pY29uLnBuZw==");
}