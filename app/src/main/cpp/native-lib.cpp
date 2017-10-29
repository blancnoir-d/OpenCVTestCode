#include <jni.h>
#include <opencv2/opencv.hpp>
#include <android/asset_manager_jni.h>
#include <android/log.h>



using namespace cv;
using namespace std;

extern "C" {
//copyFile에서 디바이스에 저장된 이미지를 로드하는
JNIEXPORT void JNICALL
Java_com_example_saeha_filtertest_Filter_loadImage(
        JNIEnv *env,
        jobject,
        jstring imagePath,
        jlong addrImage) {

    Mat &img_input = *(Mat *) addrImage;

    const char *nativeFileNameString = env->GetStringUTFChars(imagePath, JNI_FALSE);

    string baseDir;
    baseDir.append(nativeFileNameString);
    const char *pathDir = baseDir.c_str(); //이미지가 저장된 경로

    img_input = imread(pathDir, IMREAD_COLOR);


}

JNIEXPORT void JNICALL
Java_com_example_saeha_filtertest_Filter_imageprocessing(
        JNIEnv *env,
        jobject,
        jlong addrInputImage,
        jlong addrOutputImage) {

    Mat &img_input = *(Mat *) addrInputImage;
    Mat &img_output = *(Mat *) addrOutputImage;

    cvtColor( img_input, img_input, CV_BGR2RGB);
    cvtColor( img_input, img_output, CV_RGB2GRAY);
    blur( img_output, img_output, Size(5,5) );
    Canny( img_output, img_output, 50, 150, 5 );
}


JNIEXPORT void JNICALL
Java_com_example_saeha_filtertest_Filter_gray(JNIEnv *env, jobject instance, jlong addrInputImage,
                                              jlong addrOutputImage) {

    // TODO
    Mat &img_input = *(Mat *) addrInputImage;
    Mat &img_output = *(Mat *) addrOutputImage;


    cvtColor( img_input, img_input, CV_BGR2RGB);
    cvtColor(img_input, img_output, CV_RGBA2GRAY);

}

}