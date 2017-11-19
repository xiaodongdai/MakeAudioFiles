#!/bin/bash
cd eng-GBR-Queenelizabeth/
rm -rf *trimmed.wav
rm -rf *.mp3
for i in *.wav; do
  FILENAME=$i
  FILENAME=${FILENAME%.wav}
  TRIMFILE=${FILENAME}_trimmed.wav
  sox $i ${TRIMFILE} silence 0  3 0.1 8%
  ffmpeg -y -i ${TRIMFILE} -vn -ar 44100 -ac 1 -ab 64k -f mp3 eng_gbr_queenelizabeth_$FILENAME.mp3
done
cd ..
cd eng-IND-Deepa/
rm -rf *trimmed.wav
rm -rf *.mp3

for i in *.wav; do
  FILENAME=$i
  FILENAME=${FILENAME%.wav}
  TRIMFILE=${FILENAME}_trimmed.wav
  sox $i ${TRIMFILE} silence 0  3 0.1 8%
  ffmpeg -y -i ${TRIMFILE} -vn -ar 44100 -ac 1 -ab 64k -f mp3 eng_ind_deepa_$FILENAME.mp3
done
cd ..

