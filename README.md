# Machine Learning Branch of BaGi App

## 1. Object Detection Model

For object Detection model, we created model made from scratch

**Model Made From Scratch**<br />
We create basic Object Detection model with 4 layers of convolutional neural network. With dataset around 1337 total images for 4 classes, we achieved **0.8951 validation accuracy** and **0.7365 validation loss**. The dataset we use is far from sufficient to create a good model and we still struggled on handling the overfitting after several times tuning the model.<br />

![image](Contents/Model1_Val_Acc.jpg)

**Model with transfer learning using InceptionV3**<br />
For the second model, we tried transfer learning using InceptionV3 model. InceptionV3 is an upgraded version from InceptionV1 which was introduced as GoogLeNet in 2014. As the name suggests it was developed by a team at Google. InceptionV3 architecture consist of Convolutional Neural Networks with 48 layers deep. With InceptionV3, our model performance is significantly better despite having small amount of dataset. After 40 epoch, our model quickly achieved **0.9865 validation accuracy** and **0.0617 validation loss**

![image](Contents/Model2_Val_Acc.jpg)


## 2. Landmark Image Dataset

We collected dataset manually by scraping from Google images. In total, we accumulated 2067 images for 15 landmarks. The distribution as follows:

1. Lawang Sewu : 115
2. Candi Borobudur : 131
3. Candi Prambanan : 194
4. Masjid Agung Jawa Tengah : 111
5. Tugu Jogja : 100
6. Monjali : 88
7. Besakih : 200
8. Suroboyo : 216
9. Garuda Wisnu Kencana : 100
10. Monumen Nasional : 146
11. Gedung Sate : 140
12. Jam Gadang : 115
13. Sam Poo Kong : 148
14. Taman Ayun : 130
15. Museum Fatahillah : 134

Due to time limit and unavailable nearby tourism dataset for certain landmark, we only put 6 landmark in our final product which focused on landmark from Yogyakarta & Central Java (Lawang Sewu, Borobudur, Prambanan, Masjid Agung, Tugu Jogja, and Monjali).

**Processing Technique**<br />

We use [Download All Images](https://chrome.google.com/webstore/detail/download-all-images/ifipmflagepipjokmbdecpmjbibjnakm?hl=en) chrome extension to download multiple images from Google simultaneously. After that, we can quickly detect duplicate images and remove them automatically using Image Hashing & Hamming Distance method with Python script from this [repository](https://github.com/moondra2017/Computer-Vision) (Credit : [moondra2017](https://github.com/moondra2017))

## 3. Tourism Destination Dataset

We use Indonesia Tourism Destination dataset to get various information about tourism destination including landmark such as description, rate, coordinate, ticket price, etc. This dataset is used to create a list of closest nearby tourism destination from the detected landmark and put them in google map API. Futhermore, we use this dataset to create description and full information about each tourism destionation in the application. Dataset taken from Kaggle & can be accessed through this [link](https://www.kaggle.com/datasets/aprabowo/indonesia-tourism-destination). 

## 4. TFLite Model

For model deployment, we convert .h model into TFLite to make it smaller and compatible for mobile device. Our final TFLite model has size of 60mb & can be accessed through this [Google drive](https://drive.google.com/drive/folders/1jzKucwzypVAmtW5rCzH4nj8Bdu1IsWSZ?usp=sharing). 
