#! /bin/sh

set -ex

git push origin staging

git checkout development
git merge staging
git push origin development

git checkout master 
git merge development
git push origin master

git checkout staging

