#! /bin/bash

set -ex

tar -zxvf secretfiles.tar.gz
mv temp/google-services.json app/
mv temp/keystore.jks .

# ./gradlew test

if [ "$TRAVIS_BRANCH" = "master" ] ; then
  if [[ "$TRAVIS_TAG" =~ ^[0-9\.]+$ ]] ; then
    echo "Start production build."
    ./gradlew generateLicensePage
    ./gradlew assembleProduction;
    jarsigner \
      -verbose \
      -storepass $KEYSTORE_ALIAS_PASSWORD \
      -keypass $KEYSTORE_PASSWORD \
      -sigalg SHA1withRSA \
      -digestalg SHA1 \
      -keystore keystore.jks \
      -signedjar app/production/release/app-production-release.apk \
      app/build/outputs/apk/production/release/app-production-release-unsigned.apk \
      $KEYSTORE_ALIAS;
    ./gradlew uploadDeployGateProductionRelease;
  else
    echo "Start staging build."
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
  fi
fi
