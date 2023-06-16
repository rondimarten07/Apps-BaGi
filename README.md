# Machine Learning Branch of BaGi App

## 1. Object Detection Model

For object Detection model, we created model made from scratch

**Model Made From Scratch**<br />
We create basic Object Detection model with 4 layers of convolutional neural network. With dataset around 1337 total images for 4 classes, we achieved **0.8951 validation accuracy** and **0.7365 validation loss**. The dataset we use is far from sufficient to create a good model and we still struggled on handling the overfitting after several times tuning the model.<br />

![train_and_val_acc.jpg](https://github.com/rondimarten07/Apps-BaGi/blob/1b796ee43d113196a45d4dca1ef854a675d2f259/train_and_val_acc.jpg)

## 2. Shirt, Shoes, Bag, and the Defected Dataset

We collected dataset manually by scraping from Kaggle, Google images, and Roboflow. Also we label all of the image using roboflow for annotation. In total, we accumulated 1337 images

## 3. TFLite Model

For model deployment, we convert .h model into TFLite to make it smaller and compatible for mobile device. Our final TFLite model has size of 60mb & can be accessed through this [Google drive](https://drive.google.com/drive/folders/1jzKucwzypVAmtW5rCzH4nj8Bdu1IsWSZ?usp=sharing). 
