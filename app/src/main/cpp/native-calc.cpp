#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_felan_nativecalculator_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT double JNICALL
Java_com_felan_nativecalculator_models_Operator_add(
        JNIEnv *env,
        jobject /* this */,
        jdouble first,
        jdouble second) {
    return first + second;
}

extern "C" JNIEXPORT double JNICALL
Java_com_felan_nativecalculator_models_Operator_sub(
        JNIEnv *env,
        jobject /* this */,
        jdouble first,
        jdouble second) {
    return first - second;
}

extern "C" JNIEXPORT double JNICALL
Java_com_felan_nativecalculator_models_Operator_mul(
        JNIEnv *env,
        jobject /* this */,
        jdouble first,
        jdouble second) {
    return first * second;
}

#define OPERATOR_CLASS_PATH "com/felan/nativecalculator/models/Operator"
#define OPERATOR_OPERATE_METHOD_NAME "operate"
#define CALCABLE_CLASS_PATH "com/felan/nativecalculator/models/Calcable"
#define CALCABLE_CALC_METHOD_NAME "calc"


extern "C" JNIEXPORT double JNICALL
Java_com_felan_nativecalculator_models_StackStatement_nativeCalc(
        JNIEnv *env,
        jobject /* this */,
        jobjectArray objects) {
    jclass operatorClass = (*env).FindClass(OPERATOR_CLASS_PATH);
    jmethodID operateMethod = (*env).GetMethodID(operatorClass, OPERATOR_OPERATE_METHOD_NAME, "(DD)D");
    assert(operatorClass != nullptr);
    assert(operateMethod != nullptr);
    jclass calcableClass = (*env).FindClass(CALCABLE_CLASS_PATH);
    jmethodID calcMethod = (*env).GetMethodID(calcableClass, CALCABLE_CALC_METHOD_NAME, "()D");
    assert(calcableClass != nullptr);
    assert(calcMethod != nullptr);
    int size = (*env).GetArrayLength(objects);
    int i = 0;
    double result = 0;
    while (i < size) {
        jobject pop = (*env).GetObjectArrayElement(objects, i++);
        if ((*env).IsInstanceOf(pop, operatorClass))
            result = (*env).CallDoubleMethod(pop, operateMethod, result,
                                             (*env).CallDoubleMethod((*env).GetObjectArrayElement(objects, i++),
                                                                     calcMethod));
        else
            result = (*env).CallDoubleMethod(pop, calcMethod);
    }
    return result;
}