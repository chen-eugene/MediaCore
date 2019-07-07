#### 1、JNIEnv类型和jobject类型。

  - **JNIEnv**：代表Java环境，通过**JNIEnv*** 指针可以操作Java端代码。
  
  - **jobject**：如果native方法不是static，就代表native方法的类实例；如果native方法是static，那么就代表native方法的类的class对象实例。
  
  - **jclass**：为了能够在C/C++中使用Java类，jni.h头文件专门定义了jclass类型来表示Java中的Class类。JNIEnv中有如下几个函数可以获取到jclass：
    - jclas FindClass(const char* clsName)：通过类的名称（类的全名，这时候包名不是用点号而是用/来区分的）来获取jclass：`如：jclass str = env -> FindClass(java/lang/String);`
    
    - jclass GetObjectClass(jobject obj)：通过对象实例来获取jclass，相当于Java中的getClass方法。
    
    - jclass GetSuperClass(jclass obj)：通过jclass可以获取其父类的jclass对象。
<br>

#### 2、Java类型和native类型的映射关系。

| Java类型      |     native类型    |   JNI定义的别名  |
| :-------- | :--------| :------ |
| int | long |  jint/jsize |
| long | __int64 | jlong |
| byte | signed char | jbyte |
| boolean | unsigned char | jboolean |
| char | unsigned short | jchar |
| short | short | jshort |
| float | float | jfloat |
| double | double | jdouble |
| Object | __jobject* | jobject |  
<br>

#### 3、native中调用Java层代码。

  JNI在jni.h头文件中定义了jfieldId、jmethodId类型来分别代表Java端的属性和方法。在访问或者设置属性的时候，首先需要在native代码中取得代表Java属性的jfieldId，然后才能在本地代码中进行Java属性的操作。同样的，需要先取得代表该方法的jmethodId才能进行Java方法调用。
  
  - GetFieldId/GetStaticFieldId(jclass clazz,char* name,const char* sign)
    - clazz：方法依赖的类对象的class对象
    - name：字段的名字
    - sign：字段的签名

  - GetMethodId/GetStaticMethodId(jclass clazz,char* name,const char* sign)
  
  ```
  public class Hello{
    public int property;
    
    public int function(int foo,Date date,int[] arr){
    }
    
    public native void test();
  }
  
  JNIEXPORT void Java_Hello_test(JNIEnv* env,jobject obj){
    //因为test不是静态方法，所以传进来的就是调用这个函数的对象
    //否则就传入一个jclass对象表示native方法所在的类
    jclass hello_clazz = env -> GetObjectClass(obj);
    jfieldId fieldId_prop = env -> GetFieldId(hello_clazz,"property","I");
    jmethodId methodId_prop = env -> GetMethodId(hello_clazz,"function","(ILjava/util/Date;[I)I");
  }
  ```
  &nbsp;&nbsp;签名对照表：可以使用javap命令查看签名，如：javap -s -p JNIDemo.class      
  
  | Java类型      |     相应的签名    | Java类型      |     相应的签名    |
  | :-------- | :--------| :--------| :--------|
  | boolean | Z | float | F |
  | byte | B | double | D |
  | char | C | void | V |
  | short | S | object | L如：Ljava/lang/String |
  | int | I | Array | [签名  如：[I |
  | long | L | Method | (参数签名)返回值类型签名 |
<br>

#### [1、native方法的静态注册和动态注册。](https://blog.csdn.net/XSF50717/article/details/54693802)

  - 静态注册：native函数的命名规则：Java_类全路径_方法名。
  - 动态注册：JNI 允许我们提供一个函数映射表，注册给 JVM，这样 JVM 就可以用函数映射表来调用相应的函数，而不必通过函数名来查找相关函数(这个查找效率很低，函数名超级长)。
  
    - 利用结构体JNINativeMethod保存Java Native函数和JNI函数的对应关系；
    - 在一个JNINativeMethod数组中保存所有native函数和JNI函数的对应关系；
    - 在Java中通过System.loadLibrary加载完JNI动态库之后，调用JNI_OnLoad函数，开始动态注册；
    - JNI_OnLoad中会调用AndroidRuntime::registerNativeMethods函数进行函数注册；
    - AndroidRuntime::registerNativeMethods中最终调用jniRegisterNativeMethods完成注册。


#### [2、JNIEXPORT 和 JNICALL宏的作用。](https://stackoverflow.com/questions/19422660/when-to-use-jniexport-and-jnicall-in-android-ndk)

  



  




