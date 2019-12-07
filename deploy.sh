#! /bin/bash

set -ex

tar -zxvf secretfiles.tar.gz
mv temp/google-services.json app/
mv temp/keystore.jks .

# ./gradlew test

if [[ "$TAVIS_TAG" =~ ^[0-9\.]+$ ]] ; then
  echo "Start production build."
  ./gradlew generateLicensePage
  ./gradlew assembleDevelopment;
  jarsigner \
    -verbose \
    -storepass $KEYSTORE_ALIAS_PASSWORD \
    -keypass $KEYSTORE_PASSWORD \
    -sigalg SHA1withRSA \
    -digestalg SHA1 \
    -keystore keystore.jks \
    -signedjar app/development/release/app-development-release.apk \
    app/build/outputs/apk/development/release/app-development-release-unsigned.apk \
    $KEYSTORE_ALIAS;
  ./gradlew uploadDeployGateDevelopmentRelease;
elif [[ "$TRAVIS_TAG" =~ ^[0-9\.]+ ]] ; then
  echo "Start staging build."
  ./gradlew generateLicensePage
  ./gradlew generateLicensePage
  ./gradlew assembleStaging;
  jarsigner \
    -verbose \
    -storepass $KEYSTORE_ALIAS_PASSWORD \
    -keypass $KEYSTORE_PASSWORD \
    -sigalg SHA1withRSA \
    -digestalg SHA1 \
    -keystore keystore.jks \
    -signedjar app/staging/release/app-staging-release.apk \
    app/build/outputs/apk/staging/release/app-staging-release-unsigned.apk \
    $KEYSTORE_ALIAS;
  ./gradlew uploadDeployGateStagingRelease;
else
  echo "Releasing is not triggered.."
  ./gradlew generateLicensePage
fi

