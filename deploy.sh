#! /bin/sh

set -e

tar -zxvf secretfiles.tar.gz
mv temp/google-services.json app/
mv temp/keystore.jks .

./gradlew test

if [ "$TRAVIS_BRANCH" = "master" ]; then
  ./gradlew generateLicensePage

  echo "Do nothing on master branch."
  exit 0;
fi

if [ "$TRAVIS_BRANCH" = "development" ]; then
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
fi

if [ "$TRAVIS_BRANCH" = "staging" ]; then
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
fi


