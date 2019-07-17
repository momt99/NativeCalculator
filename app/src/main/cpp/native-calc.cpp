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
#define OPERATOR_SYMBOL_FIELD_NAME "symbol"

#define CALCABLE_CLASS_PATH "com/felan/nativecalculator/models/Calcable"
#define CALCABLE_CALC_METHOD_NAME "calc"


#define RAW_VALUE_CLASS_PATH "com/felan/nativecalculator/models/RawValue"

#define RAW_VALUE_VALUE_FIELD "value"

#define STACK_STATEMENT_CLASS_PATH "com/felan/nativecalculator/models/StackStatement"
#define STACK_STATEMENT_ITEMS_FIELD "items"

#define ARRAY_LIST_CLASS_PATH "java/util/ArrayList"
#define ARRAY_LIST_ELEMENT_DATA_FIELD "elementData"
#define ARRAY_LIST_SIZE_FIELD  "size"

double
operate_the_symbol(JNIEnv *env,
                   jchar symbol,
                   double first, double second) {
    switch (symbol) {
        case '+':
            return first + second;
        case '-':
            return first - second;
        case '*':
            return first * second;
        default:
            return 0;
    }
}

extern "C" JNIEXPORT double JNICALL
Java_com_felan_nativecalculator_models_StackStatement_nativeCalc(
        JNIEnv *env,
        jobject /* this */,
        jobjectArray objects,
        int size);

double calc_calcable(JNIEnv *env,
                     jobject calcable) {
    jclass rawValueClass = (*env).FindClass(RAW_VALUE_CLASS_PATH);
    jclass stackStatementClass = (*env).FindClass(STACK_STATEMENT_CLASS_PATH);
    assert(rawValueClass != nullptr);
    assert(stackStatementClass != nullptr);
    if ((*env).IsInstanceOf(calcable, rawValueClass)) {
        jfieldID valueField = (*env).GetFieldID(rawValueClass, RAW_VALUE_VALUE_FIELD, "D");
        return (*env).GetDoubleField(calcable, valueField);
    } else  //StackStatement
    {
        static jfieldID itemsField = (*env).GetFieldID(stackStatementClass, STACK_STATEMENT_ITEMS_FIELD,
                                                       "Ljava/util/ArrayList;");
        jobject items = (*env).GetObjectField(calcable, itemsField);
        jclass arrayListClass = (*env).FindClass(ARRAY_LIST_CLASS_PATH);
        jfieldID elementDataField = (*env).GetFieldID(arrayListClass, ARRAY_LIST_ELEMENT_DATA_FIELD,
                                                      "[Ljava/lang/Object;");
        jfieldID sizeField = (*env).GetFieldID(arrayListClass, ARRAY_LIST_SIZE_FIELD, "I");
        return Java_com_felan_nativecalculator_models_StackStatement_nativeCalc(env,
                                                                                calcable,
                                                                                static_cast<jobjectArray>((*env).GetObjectField(
                                                                                        items, elementDataField)),
                                                                                (*env).GetIntField(items, sizeField));
    }
}


extern "C" JNIEXPORT double JNICALL
Java_com_felan_nativecalculator_models_StackStatement_nativeCalc(
        JNIEnv *env,
        jobject /* this */,
        jobjectArray objects,
        int size) {
    jclass operatorClass = (*env).FindClass(OPERATOR_CLASS_PATH);
    jfieldID symbolField = (*env).GetFieldID(operatorClass, OPERATOR_SYMBOL_FIELD_NAME, "C");
    assert(operatorClass != nullptr);
    assert(symbolField != nullptr);
    jclass calcableClass = (*env).FindClass(CALCABLE_CLASS_PATH);
    jmethodID calcMethod = (*env).GetMethodID(calcableClass, CALCABLE_CALC_METHOD_NAME, "()D");
    assert(calcableClass != nullptr);
    assert(calcMethod != nullptr);
    int i = 0;
    double result = 0;
    while (i < size) {
        jobject pop = (*env).GetObjectArrayElement(objects, i++);
        if ((*env).IsInstanceOf(pop, operatorClass))
            result = operate_the_symbol(env, (*env).GetCharField(pop, symbolField), result,
                                        calc_calcable(env, (*env).GetObjectArrayElement(objects, i++)));
        else
            result = calc_calcable(env, pop);
    }
    return result;
}

