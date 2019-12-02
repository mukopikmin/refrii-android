#! /bin/sh

set -ex

DEVELOPMENT_TAG=development
STAGING_TAG=staging

tar -zxvf secretfiles.tar.gz
mv temp/google-services.json app/
mv temp/keystore.jks .

# ./gradlew test

if [ $TRAVIS_TAG = $DEVELOPMENT_TAG ] ; then
  ./gradlew generateLicensePage
  ./gradlew assembleDevelopment;
  jarsigner \
    -verbose \
    -storepass $KEYSTORE_ALIAS_PASSWORD \
    -keypass $KEYSTORE_PASSWORD \
    -sigalg SHA1withRSA \
    -digestalg SHA1 \
    -keystore keystore.jks \
    -signedjar app/${TRAVIS_BRANCH}/release/app-${TRAVIS_BRANCH}-release.apk \
    app/build/outputs/apk/${TRAVIS_BRANCH}/release/app-${TRAVIS_BRANCH}-release-unsigned.apk \
    $KEYSTORE_ALIAS;
  ./gradlew uploadDeployGateDevelopmentRelease;
elif [ $TRAVIS_TAG = $STAGING_TAG ] ; then
  ./gradlew generateLicensePage
  ./gradlew assembleStaging;
  jarsigner \
    -verbose \
    -storepass $KEYSTORE_ALIAS_PASSWORD \
    -keypass $KEYSTORE_PASSWORD \
    -sigalg SHA1withRSA \
    -digestalg SHA1 \
    -keystore keystore.jks \
    -signedjar app/${TRAVIS_BRANCH}/release/app-${TRAVIS_BRANCH}-release.apk \
    app/build/outputs/apk/${TRAVIS_BRANCH}/release/app-${TRAVIS_BRANCH}-release-unsigned.apk \
    $KEYSTORE_ALIAS;
  ./gradlew uploadDeployGateStagingRelease;
else
  echo "Do nothing on master branch."
  ./gradlew generateLicensePage
fi

