#### 1、JNIEnv类型和jobject类型。

  - **JNIEnv**：代表Java环境，通过**JNIEnv*** 指针可以操作Java端代码。
  
  - **jobject**：如果native方法不是static，就代表native方法的类实例；如果native方法是static，那么就代表native方法的类的class对象实例。
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

#### [1、native方法的静态注册和动态注册。](https://blog.csdn.net/XSF50717/article/details/54693802)

  - 静态注册：native函数的命名规则：Java_类全路径_方法名。
  - 动态注册：JNI 允许我们提供一个函数映射表，注册给 JVM，这样 JVM 就可以用函数映射表来调用相应的函数，而不必通过函数名来查找相关函数(这个查找效率很低，函数名超级长)。
  
    - 利用结构体JNINativeMethod保存Java Native函数和JNI函数的对应关系；
    - 在一个JNINativeMethod数组中保存所有native函数和JNI函数的对应关系；
    - 在Java中通过System.loadLibrary加载完JNI动态库之后，调用JNI_OnLoad函数，开始动态注册；
    - JNI_OnLoad中会调用AndroidRuntime::registerNativeMethods函数进行函数注册；
    - AndroidRuntime::registerNativeMethods中最终调用jniRegisterNativeMethods完成注册。


#### [2、JNIEXPORT 和 JNICALL宏的作用。](https://stackoverflow.com/questions/19422660/when-to-use-jniexport-and-jnicall-in-android-ndk)

  



  




