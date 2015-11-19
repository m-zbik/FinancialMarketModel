
```{r ImportData , include=FALSE}
# set working directory
getwd()
setwd("C:/rfm")

## import data from text file (all are being strings)
# all data are log(...) 
#example: averageTradePrice is in fact log(averageTradePrice)
textFileAll <- read.table(file = "timeSeriesFM.txt", sep="\t", 
                          stringsAsFactors=FALSE)
textFileAllNN <- read.table(file = "timeSeriesFMNN.txt", sep="\t", 
                            stringsAsFactors=FALSE)
textFileAllPlusNN <- read.table(file = "timeSeriesFMPlusNN.txt", sep="\t", 
                                stringsAsFactors=FALSE)


# define header of the tabel
header <- c("runID", "time", "asset", "closeAskPrice", "closeBidPrice", 
            "averageTradePrice", "volume", "return", 
            "predictionErrorPlusNN")
headerNN <- c("runID", "time", "asset", "closeAskPrice", "closeBidPrice", 
              "averageTradePriceNN", "volumeNN", "returnNN", 
              "predictionErrorNN")
headerPlusNN <- c("runID", "time", "asset", "closeAskPrice", "closeBidPrice", 
                  "averageTradePricePlusNN", "volumePlusNN", "returnPlusNN", 
                  "predictionErrorPlusNN")
colnames(textFileAll) <- header
colnames(textFileAllNN) <- headerNN
colnames(textFileAllPlusNN) <- headerPlusNN
```


```{r SortData, include=FALSE}
# "return" s¹ wartoœciami generowanymi w kodzie java. 
# Dodatkowo sprawdzam wolumen obrotów. 
## returns and errorNN
# extract returns from returnColumn
returnCol <- textFileAll[, "return"]
returnCol <- as.matrix(returnCol)
returnCol <- as.numeric(returnCol)
returnCol <- returnCol[-1]               

symulNo <- 50
trainSet <- 101
steps <- 601
x <- 0 
deleted <- 0
for (i in 1:symulNo){
        returnCol <- returnCol[-((1+x-deleted):(trainSet+x-deleted))]
        x <- x+steps
        deleted <- deleted + trainSet
}

# extract returns from returnColumn
returnColNN <- textFileAllNN[, "returnNN"]
returnColNN <- as.matrix(returnColNN)
returnColNN <- as.numeric(returnColNN)
returnColNN <- returnColNN[-1]

symulNo <- 50
trainSet <- 101
steps <- 601
x <- 0 
deleted <- 0
for (i in 1:symulNo){
        returnColNN <- returnColNN[-((1+x-deleted):(trainSet+x-deleted))]
        x <- x+steps
        deleted <- deleted + trainSet
}
# extract returns from returnColumn
returnColPlusNN <- textFileAllPlusNN[, "returnPlusNN"]
returnColPlusNN <- as.matrix(returnColPlusNN)
returnColPlusNN <- as.numeric(returnColPlusNN) 
returnColPlusNN <- returnColPlusNN[-1]

symulNo <- 50
trainSet <- 101
steps <- 601
x <- 0 
deleted <- 0
for (i in 1:symulNo){
        returnColPlusNN <- returnColPlusNN[-((1+x-deleted):(trainSet+x-deleted))]
        x <- x+steps
        deleted <- deleted + trainSet
}

# extract volume from textFileAll volume
volume <- textFileAll[, "volume"]
volume <- as.matrix(volume)
volume <- as.numeric(volume)
volume <- volume[-1]

symulNo <- 50
trainSet <- 101
steps <- 601
x <- 0 
deleted <- 0
for (i in 1:symulNo){
        volume <- volume[-((1+x-deleted):(trainSet+x-deleted))]
        x <- x+steps
        deleted <- deleted + trainSet
}

# extract volume from textFileAllNN volume
volumeNN <- textFileAllNN[, "volumeNN"]
volumeNN <- as.matrix(volumeNN)
volumeNN <- as.numeric(volumeNN)
volumeNN <- volumeNN[-1]

symulNo <- 50
trainSet <- 101
steps <- 601
x <- 0 
deleted <- 0
for (i in 1:symulNo){
        volumeNN <- volumeNN[-((1+x-deleted):(trainSet+x-deleted))]
        x <- x+steps
        deleted <- deleted + trainSet
}

# extract volume from textFileAllPlusNN volume
volumePlusNN <- textFileAllPlusNN[, "volumePlusNN"]
volumePlusNN <- as.matrix(volumePlusNN)
volumePlusNN <- as.numeric(volumePlusNN)
volumePlusNN <- volumePlusNN[-1]


symulNo <- 50
trainSet <- 101
steps <- 601
x <- 0 
deleted <- 0
for (i in 1:symulNo){
        volumePlusNN <- volumePlusNN[-((1+x-deleted):(trainSet+x-deleted))]
        x <- x+steps
        deleted <- deleted + trainSet
}

#####
# #predictionError for textFileAll does not exist (no NN)
# 
# # extract prediction erroNN from textFileAllNN
# predictionErrorNN <- textFileAllNN[, "predictionErrorNN"]
# predictionErrorNN <- as.matrix(predictionErrorNN)
# predictionErrorNN <- as.numeric(predictionErrorNN)
# 
# # extract prediction errorNN from textFileAllPlusNN
# predictionErrorPlusNN <- textFileAllPlusNN[, "predictionErrorPlusNN"]
# predictionErrorPlusNN <- as.matrix(predictionErrorPlusNN)
# predictionErrorPlusNN <- as.numeric(predictionErrorPlusNN)
# 
# # clear first 100 step from each simulation
# symulNo <- 50 
# numberOfTrainingDataInNN <- (1:500)
# 
# for i:symulNo
# returnCol <- returnCol[-numberOfTrainingDataInNN]
# returnColNN <- returnColNN[-numberOfTrainingDataInNN]
# returnColPlusNN <- returnColPlusNN[-numberOfTrainingDataInNN]
# 
# volume <- volume[-numberOfTrainingDataInNN]
# volumeNN <- volumeNN[-numberOfTrainingDataInNN]
# volumePlusNN <- volumePlusNN[-numberOfTrainingDataInNN]
#####

head(returnCol)
head(returnColNN)
head(returnColPlusNN)

head(volume)
head(volumeNN)
head(volumePlusNN)

head(predictionErrorNN)
head(predictionErrorPlusNN)

summary(returnCol)
summary(returnColNN)
summary(returnColPlusNN)

summary(volume)
summary(volumeNN)
summary(volumePlusNN)

# summary(predictionErrorNN)
# summary(predictionErrorPlusNN)
```

####
# # Czyszczenie danych
# 
# ```{r SeparateImportantData, include=FALSE}
# ## combine two returns and volume
# numberOfTrainingDataInNN <- (1:500)
# 
# returnCol <- returnCol[-numberOfTrainingDataInNN]
# returnColNN <- returnColNN[-numberOfTrainingDataInNN]
# returnColPlusNN <- returnColPlusNN[-numberOfTrainingDataInNN]
# 
# volume <- volume[-numberOfTrainingDataInNN]
# volumeNN <- volumeNN[-numberOfTrainingDataInNN]
# volumePlusNN <- volumePlusNN[-numberOfTrainingDataInNN]
# 
# predictionErrorNN <- predictionErrorNN[-numberOfTrainingDataInNN]
# predictionErrorPlusNN <- predictionErrorPlusNN[-numberOfTrainingDataInNN]
####

length(returnCol)
length(returnColNN)
length(returnColPlusNN)

length(volume)
length(volumeNN)
length(volumePlusNN)

length(predictionErrorNN)
length(predictionErrorPlusNN)

returnFMwithNan <- cbind(returnCol, returnColNN,returnColPlusNN, 
                         volume, volumeNN, volumePlusNN)
# returnFMwithNan <- cbind(returnCol, returnColNN,
#                          volume, volumeNN)

# errorWithNan <- cbind(predictionErrorNN, predictionErrorPlusNN)

# how many nan's there are int the data set
sum(is.na(returnFMwithNan))
# sum(is.na(returnFMwithNan[,"returnCol"]))
# sum(is.na(returnFMwithNan[,"returnColNN"]))
# sum(is.na(returnFMwithNan[,"returnColPlusNN"]))
# sum(is.na(returnFMwithNan[, "volume"]))
# sum(is.na(returnFMwithNan[, "volumeNN"]))
# sum(is.na(returnFMwithNan[, "volumePlusNN"]))
# sum(is.na(errorWithNan[, "predictionErrorNN"]))
# sum(is.na(errorWithNan[, "predictionErrorPlusNN"]))

# what percentage of all it is:
# (sum(is.na(returnFMwithNan))/length(returnFMwithNan)) * 100
# If there are any NaN's than eliminate tham by:check which rows contain nan's
# returnFMNanTrueReturnCol <- 
#                 is.na(returnFMwithNan[,"returnsFromAvTradePr"]) == TRUE
# returnFMNanTrueReturnColNN <- 
#                 is.na(returnFMwithNan[,"returnsFromAvTradePrNN"]) == TRUE
# returnFMNanTrueReturnColPlusNN <- 
#                 is.na(returnFMwithNan[,"returnsFromAvTradePrPlusNN"]) == TRUE
# # which row contain nan
# rowIndexWithNan <- which(returnFMNanTrueReturnCol)
# rowIndexWithNan <- which(returnFMNanTrueReturnColNN)
# rowIndexWithNan <- which(returnFMNanTrueReturnColPlusNN)
# # clear from Nan's
returnFM <- returnFMwithNan 

# sum(is.na(errorWithNan))
# error <- errorWithNan
# returnFM <- returnFMwithNan[-rowIndexWithNan, ]
# returnFM <- returnFMwithNan[-rowIndexWithNanNN, ]
# returnFM <- returnFMwithNan[-rowIndexWithNanPlusNN, ]

dim(returnFM)
# dim(error)
```

# create matric 500steps x 50simulatains

```{r}
symulNo <- 50
steps <- 500
newRFM <- matrix( rep(1, each=25000), nrow=50, ncol=500) 
newRFMNN <- matrix( rep(1, each=25000), nrow=50, ncol=500)
newRFMPlusNN <- matrix( rep(1, each=25000), nrow=50, ncol=500)
newVol <- matrix( rep(1, each=25000), nrow=50, ncol=500)
newVolNN <- matrix( rep(1, each=25000), nrow=50, ncol=500)
newVolPlusNN <- matrix( rep(1, each=25000), nrow=50, ncol=500)

# newRFM
x <- 0
for (i in 1:symulNo){
        for (j in 1:steps){
                newRFM[i, j] <- returnCol[j+x]
        }
        x <- x + steps
}

# newRFMNN
x <- 0
for (i in 1:symulNo){
        for (j in 1:steps){
                newRFMNN[i, j] <- returnColNN[j+x]
        }
        x <- x + steps
}

# newRFMPlusNN
x <- 0
for (i in 1:symulNo){
        for (j in 1:steps){
                newRFMPlusNN[i, j] <- returnColPlusNN[j+x]
        }
        x <- x + steps
}

# newVol
x <- 0
for (i in 1:symulNo){
        for (j in 1:steps){
                newVol[i, j] <- volume[j+x]
        }
        x <- x + steps
}

# newVolNN
x <- 0
for (i in 1:symulNo){
        for (j in 1:steps){
                newVolNN[i, j] <- volumeNN[j+x]
        }
        x <- x + steps
}

# newVolPlusNN
x <- 0
for (i in 1:symulNo){
        for (j in 1:steps){
                newVolPlusNN[i, j] <- volumePlusNN[j+x]
        }
        x <- x + steps
}
```

```{r}
# 
# Utworzenie zbioru wartoœci œrednich, mediany i odchyleñ st dla ka¿dego kroku 
# z 50 symulacji
rFM <- apply(newRFM, 2, mean)
rFMMedian <- apply(newRFM, 2, median)
rFMStDev <- apply(newRFM, 2, sd)

rFMNN <- apply(newRFMNN, 2, mean)
rFMNNMedian <- apply(newRFMNN, 2, median)
rFMNNStDev <- apply(newRFMNN, 2, sd)

rFMPlusNN <- apply(newRFMPlusNN , 2, mean)
rFMPlusNNMedian <- apply(newRFMPlusNN , 2, median)
rFMPlusNNStDev <- apply(newRFMPlusNN , 2, sd)

vFM <- apply(newVol, 2, mean)
vFMMedian <- apply(newVol, 2, median)
vFMStDev <- apply(newVol, 2, sd)

vFMNN <- apply(newVolNN, 2, mean)
vFMNNMedian <- apply(newVolNN, 2, median)
vFMNNStDev <- apply(newVolNN, 2, sd)

vFMPlusNN <- apply(newVolPlusNN, 2, mean)
vFMPlusNNMedian <- apply(newVolPlusNN, 2, median)
vFMPlusNNStDev <- apply(newVolPlusNN, 2, sd)
```

```{r}
# con
library(boot) 
mean.boot = function(x, idx) {
        # arguments:
        # x                 data to be resampled
        # idx                vector of scrambled indices created by boot() function
        # value:
        # ans        	mean value computed using resampled data
        ans = mean(x[idx])
        ans
}

rFMConfLevelH <- rep(1, each=500)
rFMConfLevelL <- rep(1, each=500)
rFMNNConfLevelH <- rep(1, each=500)
rFMNNConfLevelL <- rep(1, each=500)
rFMPlusNNConfLevelH <- rep(1, each=500)
rFMPlusNNConfLevelL <- rep(1, each=500)

# conf Levels for each model (low, high) based od quntiles
steps <- 500

for (i in 1:steps){
        meanBootRFMEachColumn <- boot(newRFM[, i], statistic = mean.boot, R=999)
        mubootCIrFMEachColumn <- boot.ci(meanBootRFMEachColumn, conf = 0.95, type = c("norm","perc"))
        rFMConfLevelH[i] <- mubootCIrFMEachColumn$percent[,5]
        rFMConfLevelL[i] <- mubootCIrFMEachColumn$percent[,4]    
}

for (i in 1:steps){
        meanBootRFMNNEachColumn <- boot(newRFMNN[, i], statistic = mean.boot, R=999)
        mubootCIrFMNNEachColumn <- boot.ci(meanBootRFMNNEachColumn, conf = 0.95, type = c("norm","perc"))
        rFMNNConfLevelH[i] <- mubootCIrFMNNEachColumn$percent[,5]
        rFMNNConfLevelL[i] <- mubootCIrFMNNEachColumn$percent[,4]    
}

for (i in 1:steps){
        meanBootRFMPlusNNEachColumn <- boot(newRFMPlusNN[, i], statistic = mean.boot, R=999)
        mubootCIrFMPlusNNEachColumn <- boot.ci(meanBootRFMPlusNNEachColumn, conf = 0.95, type = c("norm","perc"))
        rFMPlusNNConfLevelH[i] <- mubootCIrFMPlusNNEachColumn$percent[,5]
        rFMPlusNNConfLevelL[i] <- mubootCIrFMPlusNNEachColumn$percent[,4]    
}


```
```{r}
library("ggplot2")
# plot of 5% confidence level (qwnatile 0,025 and quantile 0,975) for
# rFM, rFMNN, rFMPlusNN
        
rFMConfLevelHDataFrame <- as.data.frame(rFMConfLevelH)
rFMConfLevelHDataFrame$step <- c(1:length(rFMConfLevelH))
rFMConfLevelLDataFrame <- as.data.frame(rFMConfLevelL)
rFMConfLevelLDataFrame$step <- c(1:length(rFMConfLevelL))

rFMNNConfLevelHDataFrame <- as.data.frame(rFMNNConfLevelH)
rFMNNConfLevelHDataFrame$step <- c(1:length(rFMNNConfLevelH))
rFMNNConfLevelLDataFrame <- as.data.frame(rFMNNConfLevelL)
rFMNNConfLevelLDataFrame$step <- c(1:length(rFMNNConfLevelL))

rFMPlusNNConfLevelHDataFrame <- as.data.frame(rFMPlusNNConfLevelH)
rFMPlusNNConfLevelHDataFrame$step <- c(1:length(rFMPlusNNConfLevelH))
rFMPlusNNConfLevelLDataFrame <- as.data.frame(rFMPlusNNConfLevelL)
rFMPlusNNConfLevelLDataFrame$step <- c(1:length(rFMPlusNNConfLevelL))

ConfLevelggplot <- ggplot() + 
        geom_line(data = rFMConfLevelHDataFrame, aes(x = step, y = rFMConfLevelH, color="#CC79A7")) +
        geom_line(data = rFMConfLevelLDataFrame, aes(x = step, y = rFMConfLevelL, color="#CC79A7")) +
        geom_line(data = rFMNNConfLevelHDataFrame, aes(x = step, y = rFMNNConfLevelH, color="#0072B2")) +
        geom_line(data = rFMNNConfLevelLDataFrame, aes(x = step, y = rFMNNConfLevelL, color="#0072B2")) +
        geom_line(data = rFMPlusNNConfLevelHDataFrame, aes(x = step, y = rFMPlusNNConfLevelH, color="#009E73"))  +
        geom_line(data = rFMPlusNNConfLevelLDataFrame, aes(x = step, y = rFMPlusNNConfLevelL, color="#009E73"))  +
        ylab('Wartoœci przedzia³u ufnoœci stóp zwrotu') +
        xlab('Krok czasowy')+
        ggtitle("Górne i dolen wartoœci 5% -otwego przedzia³ ufnoœci wartoœci œrednich
                stóp zwrotu dla modeli FM, FMNN, FMPlusNN z 50 symulacji") +
        theme(plot.title=element_text(size=8, face="bold", 
                                      hjust = 0.5), 
              axis.title=element_text(size=8))+
        scale_color_discrete(name="Modele",
                             breaks=c("#CC79A7", "#0072B2", "#009E73"),
                             labels=c("FM", "FMNN", "FMPlusNN"))

```

```{r}
pdf('figure/ConfLevelggplot.pdf')
ConfLevelggplot
dev.off()
```

```{r}
# Plot Mean, Median and StDev on one plot
library("mvtnorm")
# library("ggplot2")
# multiplot function

library(grid)
# multiplot function has been created by Winston Chang and the cod has been
# copied from: 
# http://www.cookbook-r.com/Graphs/Multiple_graphs_on_one_page_(ggplot2)/
multiplot <- function(..., plotlist=NULL, file, cols=1, layout=NULL) {
        require(grid)
        
        # Make a list from the ... arguments and plotlist
        plots <- c(list(...), plotlist)
        
        numPlots = length(plots)
        
        # If layout is NULL, then use 'cols' to determine layout
        if (is.null(layout)) {
                # Make the panel
                # ncol: Number of columns of plots
                # nrow: Number of rows needed, calculated from # of cols
                layout <- matrix(seq(1, cols * ceiling(numPlots/cols)),
                                 ncol = cols, nrow = ceiling(numPlots/cols))
        }
        
        if (numPlots==1) {
                print(plots[[1]])
                
        } else {
                # Set up the page
                grid.newpage()
                pushViewport(viewport(layout = grid.layout(nrow(layout), 
                                                           ncol(layout))))
                
                # Make each plot, in the correct location
                for (i in 1:numPlots) {
                        # Get the i,j matrix positions of the regions that 
                        # contain this subplot
                        matchidx <- as.data.frame(which(layout == i, 
                                                        arr.ind = TRUE))
                        
                        print(plots[[i]], vp = viewport
                              (layout.pos.row = matchidx$row,
                               layout.pos.col = matchidx$col))
                }
        }
}




rFMDataFrame <- as.data.frame(rFM)
rFMDataFrame$step <- c(1:length(rFM))
rFMMedianDataFrame <- as.data.frame(rFMMedian)
rFMMedianDataFrame$step <- c(1:length(rFMMedian))
rFMStDevDataFrame <- as.data.frame(rFMStDev)
rFMStDevDataFrame$step <- c(1:length(rFMStDev))

#ggPlot of rFM
rFMggPlot <- ggplot(data = rFMDataFrame, aes(x = step , y = rFM))
rFMggPlot <- rFMggPlot + geom_line(colour = "red")
rFMggPlot <- rFMggPlot + geom_abline(colour = "blue", slope = 0)
rFMggPlot <- rFMggPlot + ggtitle("Stopy zwrotu z symulacji modelu z agentami 
                                 cierpliwymi i niecierpliwymi") + 
        xlab("krok czasowy") + 
        ylab("stopa zwrotu") +
        theme(plot.title=element_text(size=8, face="bold", 
                                      hjust = 0.5), 
              axis.title=element_text(size=8))
#         scale_y_continuous(breaks=seq(-.9, 1.2, 0.1))

# combine rFM, rFMMedian, rFMStDev
rFMMeanMedianSdggplot <- ggplot() + 
        geom_line(data = rFMDataFrame, aes(x = step, y = rFM, color="#CC79A7")) +
        geom_line(data = rFMMedianDataFrame, aes(x = step, y = rFMMedian, color="#0072B2")) +
        geom_line(data = rFMStDevDataFrame, aes(x = step, y = rFMStDev, color="#009E73"))  +
        ylab('Wartoœæ wielkoœci statystycznych stopy zwrotu') +
        xlab('Krok czasowy')+
        ggtitle("Œrednia, mediana i odchylenie standarowe stopy zwrotu 
                z 50 symulacji modelu FM") +
        theme(plot.title=element_text(size=8, face="bold", 
                                        hjust = 0.5), 
                                        axis.title=element_text(size=8))+
        scale_color_discrete(name="wielkoœci statystyczne",
                            breaks=c("#CC79A7", "#0072B2", "#009E73"),
                            labels=c("Œrednia", "Mediana", "Odchylenie standardowe"))

rFMNNDataFrame <- as.data.frame(rFMNN)
rFMNNDataFrame$step <- c(1:length(rFMNN))
rFMNNMedianDataFrame <- as.data.frame(rFMNNMedian)
rFMNNMedianDataFrame$step <- c(1:length(rFMNNMedian))
rFMNNStDevDataFrame <- as.data.frame(rFMNNStDev)
rFMNNStDevDataFrame$step <- c(1:length(rFMNNStDev))

#ggPlot of rFMNN
rFMNNggPlot <- ggplot(data = rFMNNDataFrame, aes(x = step , y = rFMNN))
rFMNNggPlot <- rFMNNggPlot + geom_line(colour = "red")
rFMNNggPlot <- rFMNNggPlot + geom_abline(colour = "blue", slope = 0)
rFMNNggPlot <- rFMNNggPlot + ggtitle("Stopy zwrotu z symulacji modelu z agentami 
                                 cierpliwymi i low-intelligence") + 
        xlab("krok czasowy") + 
        ylab("stopa zwrotu") +
        theme(plot.title=element_text(size=8, face="bold", 
                                      hjust = 0.5), 
              axis.title=element_text(size=8))
#         scale_y_continuous(breaks=seq(-.9, 1.2, 0.1))

# combine rFMNN, rFMNNMedian, rFMNNStDev
rFMNNMeanMedianSdggplot <- ggplot() + 
        geom_line(data = rFMNNDataFrame, aes(x = step, y = rFMNN, color="#CC79A7")) +
        geom_line(data = rFMNNMedianDataFrame, aes(x = step, y = rFMNNMedian, color="#0072B2")) +
        geom_line(data = rFMNNStDevDataFrame, aes(x = step, y = rFMNNStDev, color="#009E73"))  +
        ylab('Wartoœæ wielkoœci statystycznych stopy zwrotu') +
        xlab('Krok czasowy')+
        ggtitle("Œrednia, mediana i odchylenie standarowe stopy zwrotu 
                z 50 symulacji modelu FMNN") +
        theme(plot.title=element_text(size=8, face="bold", 
                                      hjust = 0.5), 
              axis.title=element_text(size=8))+
        scale_color_discrete(name="wielkoœci statystyczne",
                             breaks=c("#CC79A7", "#0072B2", "#009E73"),
                             labels=c("Œrednia", "Mediana", "Odchylenie standardowe"))


rFMPlusNNDataFrame <- as.data.frame(rFMPlusNN)
rFMPlusNNDataFrame$step <- c(1:length(rFMPlusNN))
rFMPlusNNMedianDataFrame <- as.data.frame(rFMPlusNNMedian)
rFMPlusNNMedianDataFrame$step <- c(1:length(rFMPlusNNMedian))
rFMPlusNNStDevDataFrame <- as.data.frame(rFMPlusNNStDev)
rFMPlusNNStDevDataFrame$step <- c(1:length(rFMPlusNNStDev))

#ggPlot of rFMPlusNN
rFMPlusNNggPlot <- ggplot(data = rFMPlusNNDataFrame, aes(x = step , y = rFMPlusNN))
rFMPlusNNggPlot <- rFMPlusNNggPlot + geom_line(colour = "red")
rFMPlusNNggPlot <- rFMPlusNNggPlot + geom_abline(colour = "blue", slope = 0)
rFMPlusNNggPlot <- rFMPlusNNggPlot + ggtitle("Stopy zwrotu z symulacji modelu z agentami 
                                     cierpliwymi, niecierpliwymi i low-intelligence") + 
        xlab("krok czasowy") + 
        ylab("stopa zwrotu") +
        theme(plot.title=element_text(size=8, face="bold", 
                                      hjust = 0.5), 
              axis.title=element_text(size=8))
#         scale_y_continuous(breaks=seq(-.9, 1.2, 0.1))

# combine rFMPlusNN, rFMPlusNNMedian, rFMPlusNNStDev
rFMPlusNNMeanMedianSdggplot <- ggplot() + 
        geom_line(data = rFMPlusNNDataFrame, aes(x = step, y = rFMPlusNN, color="#CC79A7")) +
        geom_line(data = rFMPlusNNMedianDataFrame, aes(x = step, y = rFMPlusNNMedian, color="#0072B2")) +
        geom_line(data = rFMPlusNNStDevDataFrame, aes(x = step, y = rFMPlusNNStDev, color="#009E73"))  +
        ylab('Wartoœæ wielkoœci statystycznych stopy zwrotu') +
        xlab('Krok czasowy')+
        ggtitle("Œrednia, mediana i odchylenie standarowe stopy zwrotu
                z 50 symulacji modelu FMPlusNN") +
        theme(plot.title=element_text(size=8, face="bold", 
                                      hjust = 0.5), 
              axis.title=element_text(size=8))+
        scale_color_discrete(name="wielkoœci statystyczne",
                             breaks=c("#CC79A7", "#0072B2", "#009E73"),
                             labels=c("Œrednia", "Mediana", "Odchylenie standardowe"))

```
```{r}
pdf('figure/rFMMeanMedianSdggplot.pdf')
rFMMeanMedianSdggplot
dev.off()
```
```{r}
pdf('figure/rFMNNMeanMedianSdggplot.pdf')
rFMNNMeanMedianSdggplot
dev.off()
```
```{r}
pdf('figure/rFMPlusNNMeanMedianSdggplot.pdf')
rFMPlusNNMeanMedianSdggplot
dev.off()
```

vFMDataFrame <- as.data.frame(vFM)
vFMDataFrame$step <- c(1:length(vFM))
vFMMedianDataFrame <- as.data.frame(vFMMedian)
vFMMedianDataFrame$step <- c(1:length(vFMMedian))
vFMStDevDataFrame <- as.data.frame(vFMStDev)
vFMStDevDataFrame$step <- c(1:length(vFMStDev))

# combine vFM, vFMMedian, vFMStDev
vFMMeanMedianSdggplot <- ggplot() + 
        geom_line(data = vFMDataFrame, aes(x = step, y = vFM, color="#CC79A7")) +
        geom_line(data = vFMMedianDataFrame, aes(x = step, y = vFMMedian, color="#0072B2")) +
        geom_line(data = vFMStDevDataFrame, aes(x = step, y = vFMStDev, color="#009E73"))  +
        ylab('Wartoœæ wielkoœci statystycznych wolumenu') +
        xlab('Krok czasowy')+
        ggtitle("Œrednia, mediana i odchylenie standarowe wolumenu 
                z 50 symulacji modelu FM") +
        theme(plot.title=element_text(size=8, face="bold", 
                                      hjust = 0.5), 
              axis.title=element_text(size=8))+
        scale_color_discrete(name="wielkoœci statystyczne",
                             breaks=c("#CC79A7", "#0072B2", "#009E73"),
                             labels=c("Œrednia", "Mediana", "Odchylenie standardowe"))

vFMNNDataFrame <- as.data.frame(vFMNN)
vFMNNDataFrame$step <- c(1:length(vFMNN))
vFMNNMedianDataFrame <- as.data.frame(vFMNNMedian)
vFMNNMedianDataFrame$step <- c(1:length(vFMNNMedian))
vFMNNStDevDataFrame <- as.data.frame(vFMNNStDev)
vFMNNStDevDataFrame$step <- c(1:length(vFMNNStDev))
# combine vFMNN, vFMNNMedian, vFMNNStDev
vFMNNMeanMedianSdggplot <- ggplot() + 
        geom_line(data = vFMNNDataFrame, aes(x = step, y = vFMNN, color="#CC79A7")) +
        geom_line(data = vFMNNMedianDataFrame, aes(x = step, y = vFMNNMedian, color="#0072B2")) +
        geom_line(data = vFMNNStDevDataFrame, aes(x = step, y = vFMNNStDev, color="#009E73"))  +
        ylab('Wartoœæ wielkoœci statystycznych wolumenu') +
        xlab('Krok czasowy')+
        ggtitle("Œrednia, mediana i odchylenie standarowe wolumenu 
                z 50 symulacji modelu FMNN") +
        theme(plot.title=element_text(size=8, face="bold", 
                                      hjust = 0.5), 
              axis.title=element_text(size=8))+
        scale_color_discrete(name="wielkoœci statystyczne",
                             breaks=c("#CC79A7", "#0072B2", "#009E73"),
                             labels=c("Œrednia", "Mediana", "Odchylenie standardowe"))

vFMPlusNNDataFrame <- as.data.frame(vFMPlusNN)
vFMPlusNNDataFrame$step <- c(1:length(vFMPlusNN))
vFMPlusNNMedianDataFrame <- as.data.frame(vFMPlusNNMedian)
vFMPlusNNMedianDataFrame$step <- c(1:length(vFMPlusNNMedian))
vFMPlusNNStDevDataFrame <- as.data.frame(vFMPlusNNStDev)
vFMPlusNNStDevDataFrame$step <- c(1:length(vFMPlusNNStDev))
# combine vFMPlusNN, vFMPlusNNMedian, vFMPlusNNStDev
vFMPlusNNMeanMedianSdggplot <- ggplot() + 
        geom_line(data = vFMPlusNNDataFrame, aes(x = step, y = vFMPlusNN, color="#CC79A7")) +
        geom_line(data = vFMPlusNNMedianDataFrame, aes(x = step, y = vFMPlusNNMedian, color="#0072B2")) +
        geom_line(data = vFMPlusNNStDevDataFrame, aes(x = step, y = vFMPlusNNStDev, color="#009E73"))  +
        ylab('Wartoœæ wielkoœci statystycznych wolumenu') +
        xlab('Krok czasowy')+
        ggtitle("Œrednia, mediana i odchylenie standarowe wolumenu 
                z 50 symulacji modelu FMPlusNN") +
        theme(plot.title=element_text(size=8, face="bold", 
                                      hjust = 0.5), 
              axis.title=element_text(size=8))+
        scale_color_discrete(name="wielkoœci statystyczne",
                             breaks=c("#CC79A7", "#0072B2", "#009E73"),
                             labels=c("Œrednia", "Mediana", "Odchylenie standardowe"))

```
```{r}
pdf('figure/vFMMeanMedianSdggplot.pdf')
vFMMeanMedianSdggplot
dev.off()
```
```{r}
pdf('figure/vFMNNMeanMedianSdggplot.pdf')
vFMNNMeanMedianSdggplot
dev.off()
```
```{r}
pdf('figure/vFMPlusNNMeanMedianSdggplot.pdf')
vFMPlusNNMeanMedianSdggplot
dev.off()
```

```{r}
# combine rFM, rFMNN, rFMPlusNN
rFMFMNNFMPlusNNggplot <- ggplot() + 
        geom_line(data = rFMDataFrame, aes(x = step, y = rFM, color="#CC79A7")) +
        geom_line(data = rFMNNDataFrame, aes(x = step, y = rFMNN, color="#0072B2")) +
        geom_line(data = rFMPlusNNDataFrame, aes(x = step, y = rFMPlusNN, color="#009E73")) +
        ylab('Œrednie wartoœci stóp zwrotu') +
        xlab('Krok czasowy')+
        ggtitle("Œrednie wartoœci stóp zwrotu z 50 symulacji 
                modeli FM, FMNN, FMPlusNN") +
        theme(plot.title=element_text(size=8, face="bold", 
                                      hjust = 0.5), 
              axis.title=element_text(size=8))+
        scale_color_discrete(name="Modele",
                             breaks=c("#CC79A7", "#0072B2", "#009E73"),
                             labels=c("rFM", "rFMNN", "rFMPlusNN"))

# combine vFM, vMNN, vFMPlusNN
vFMFMNNFMPlusNNggplot <- ggplot() + 
        geom_line(data = vFMDataFrame, aes(x = step, y = vFM, color="#CC79A7")) +
        geom_line(data = vFMNNDataFrame, aes(x = step, y = vFMNN, color="#0072B2")) +
        geom_line(data = vFMPlusNNDataFrame, aes(x = step, y = vFMPlusNN, color="#009E73")) +
        ylab('Œrednie wartoœci wolumenu') +
        xlab('Krok czasowy')+
        ggtitle("Œrednie wartoœci wolumenu z 50 symulacji 
                modeli FM, FMNN, FMPlusNN") +
        theme(plot.title=element_text(size=8, face="bold", 
                                      hjust = 0.5), 
              axis.title=element_text(size=8))+
        scale_color_discrete(name="Modele",
                             breaks=c("#CC79A7", "#0072B2", "#009E73"),
                             labels=c("vFM", "vFMNN", "vFMPlusNN"))

```

```{r}
pdf('figure/rFMFMNNFMPlusNNggplot.pdf')
rFMFMNNFMPlusNNggplot
dev.off()
```
```{r}
pdf('figure/vFMFMNNFMPlusNNggplot.pdf')
vFMFMNNFMPlusNNggplot
dev.off()
```


```{r}
# Histograms
rFMggHist <- ggplot(data=rFMDataFrame, aes(rFMDataFrame$rFM)) + 
        geom_histogram(aes(y =..density..), 
                       #breaks=seq(20, 50, by = 2), 
                       col="red", 
                       fill="blue", 
                       #alpha = .2
        ) + 
        geom_density(col=2) + 
        labs(title="Rozk³ad stóp zwrotu modelu z agentami
             cierpliwymi i niecierpliwymi") +
        labs(x="Wartoœæ stopy zwrotu", y="Czêstotliwoœæ wystêpowania",
             size=8)+
        theme(plot.title=element_text(size=8, face="bold"),
              axis.title=element_text(size=8))

rFMggBoxPlot <- ggplot(data=rFMDataFrame, aes(x = rFMDataFrame$step, 
                                              y = rFMDataFrame$rFM)) + 
        geom_boxplot( 
                #breaks=seq(20, 50, by = 2), 
                col="red", 
                fill="blue", 
                #alpha = .2
        ) +  
        labs(title="Wartoœci stóp zwrotu modelu 
             z agentami cierpliwymi i niecierpliwymi") +
        labs( x = "krok czasowy", y="stopa zwrotu", size=8)+
        theme(plot.title=element_text(size=8, face="bold", hjust = 0.5),
              axis.title=element_text(size=8))

rFMNNggHist <- ggplot(data=rFMNNDataFrame, aes(rFMNNDataFrame$rFMNN)) + 
        geom_histogram(aes(y =..density..), 
                       #breaks=seq(20, 50, by = 2), 
                       col="red", 
                       fill="blue", 
                       #alpha = .2
        ) + 
        geom_density(col=2) + 
        labs(title="Rozk³ad stóp zwrotu modelu z agentami 
             cierpliwymi i low-intelligence") +
        labs(x="Wartoœæ stopy zwrotu", y="Czêstotliwoœæ wystêpowania", size=8)+
        theme(plot.title=element_text(size=8, face="bold"),
              axis.title=element_text(size=8))

rFMNNggBoxPlot <- ggplot(data=rFMNNDataFrame, aes(x = rFMNNDataFrame$step, 
                                                  y = rFMNNDataFrame$rFMNN)) + 
        geom_boxplot( 
                #breaks=seq(20, 50, by = 2), 
                col="red", 
                fill="blue", 
                #alpha = .2
        ) +  
        labs(title="Wartoœci stóp zwrotu modelu z agentami 
             cierpliwymi i low-intelligence") +
        labs( x = "krok czasowy", y="stopa zwrotu", size=8)+
        theme(plot.title=element_text(size=8, face="bold", hjust=0.5),
              axis.title=element_text(size=8))

rFMPlusNNggHist <- ggplot(data=rFMPlusNNDataFrame, 
                          aes(rFMPlusNNDataFrame$rFMPlusNN)) + 
        geom_histogram(aes(y =..density..), 
                       #breaks=seq(20, 50, by = 2), 
                       col="red", 
                       fill="blue", 
                       #alpha = .2
        ) + 
        geom_density(col=2) + 
        labs(title="Rozk³ad stóp zwrotu modelu z agentami
             cierpliwymi, niecierpliwymi i low-intelligence") +
        labs(x="Wartoœæ stopy zwrotu", y="Czêstotliwoœæ wystêpowania", size=8)+
        theme(plot.title=element_text(size=8, face="bold"),
              axis.title=element_text(size=8))





```
```{r , echo=FALSE}
pdf('figure/rBoxPlotAll.pdf')
boxplot(rFM, rFMNN, rFMPlusNN,  cex=0.5,
                        col=c("blue","red","green"),
                        names=c("FM", "FMNN","FMPlusNN"),
                        main = "Wartoœci stóp zwrotu trzech modeli: FM, FMNN, FMPlusNN", cex.main=1,
                        ylab = "stopa zwrtou", cex=0.25)

dev.off()
```


```{r multiplotOfReturnsFM, echo=FALSE}
pdf('figure/multiplotOfHistogramsReturnsFMFMNNFMPlusNN.pdf')
multiplot(rFMggHist, rFMNNggHist, rFMPlusNNggHist, rows=3)
dev.off()
```
```{r multiplotOfReturnsFM, echo=FALSE}
pdf('figure/multiplotOfBoxPlotReturnsFMFMNNFMPlusNN.pdf')
multiplot(rFMggBoxPlot, rFMNNggBoxPlot, rFMPlusNNggBoxPlot, rows=3)
dev.off()
```

```{r}
vFMggHist <- ggplot(data=vFMDataFrame, aes(vFMDataFrame$vFM)) + 
        geom_histogram(aes(y =..density..), 
                       #breaks=seq(20, 50, by = 2), 
                       col="red", 
                       fill="blue", 
                       #alpha = .2
        ) + 
        geom_density(col=2) + 
        labs(title="Rozk³ad wolumenu modelu z agentami
             cierpliwymi i niecierpliwymi") +
        labs(x="Wartoœæ wolumenu", y="Czêstotliwoœæ wystêpowania")+
        theme(plot.title=element_text(size=14, face="bold"),
              axis.title=element_text(size=8))

vFMggBoxPlot <- ggplot(data=vFMDataFrame, aes(x = vFMDataFrame$step, 
                                              y = vFMDataFrame$vFM)) + 
        geom_boxplot( 
                #breaks=seq(20, 50, by = 2), 
                col="red", 
                fill="blue", 
                #alpha = .2
        ) +  
        labs(title="Wartoœæ wolumenu modelu z agentami
             cierpliwymi i niecierpliwymi") +
        labs( x = "krok czasowy", y="wolumen")+
        theme(plot.title=element_text(size=14, face="bold"),
              axis.title=element_text(size=8))

```


```{r multiplotOfVolumeFMNNCode, include=FALSE}
#vFMNN
vFMNNggHist <- ggplot(data=vFMNNDataFrame, aes(vFMNNDataFrame$vFMNN)) + 
        geom_histogram(aes(y =..density..), 
                       #breaks=seq(20, 50, by = 2), 
                       col="red", 
                       fill="blue", 
                       #alpha = .2
        ) + 
        geom_density(col=2) + 
        labs(title="Rozk³ad wolumenu modelu z agentami
             cierpliwymi i low-intelligence") +
        labs(x="Wartoœæ wolumenu", y="Czêstotliwoœæ wystêpowania")+
        theme(plot.title=element_text(size=14, face="bold"),
              axis.title=element_text(size=8))

vFMNNggBoxPlot <- ggplot(data=vFMNNDataFrame, aes(x = vFMNNDataFrame$step, 
                                                  y = vFMNNDataFrame$vFMNN)) + 
        geom_boxplot( 
                #breaks=seq(20, 50, by = 2), 
                col="red", 
                fill="blue", 
                #alpha = .2
        ) +  
        labs(title="Wartoœci wolumenu modelu z agentami
             cierpliwymi i low-intelligence") +
        labs( x = "krok czasowy", y="wolumen")+
        theme(plot.title=element_text(size=14, face="bold"),
              axis.title=element_text(size=8))

```


```{r multiplotOfVolumeFMPlusNNCode, include=FALSE}
#vFMPlusNN
vFMPlusNNggHist <- ggplot(data=vFMPlusNNDataFrame, 
                          aes(vFMPlusNNDataFrame$vFMPlusNN)) + 
        geom_histogram(aes(y =..density..), 
                       #breaks=seq(20, 50, by = 2), 
                       col="red", 
                       fill="blue", 
                       #alpha = .2
        ) + 
        geom_density(col=2) + 
        labs(title="Rozk³ad wolumenu modelu z agentami
             cierpliwymi, niecierpliwymi i low-intelligence") +
        labs(x="Wartoœæ wolumenu", y="Czêstotliwoœæ wystêpowania")+
        theme(plot.title=element_text(size=14, face="bold"),
              axis.title=element_text(size=8))

vFMPlusNNggBoxPlot <- ggplot(data=vFMPlusNNDataFrame, 
                             aes(x = vFMPlusNNDataFrame$step, 
                                 y = vFMPlusNNDataFrame$vFMPlusNN)) + 
        geom_boxplot( 
                #breaks=seq(20, 50, by = 2), 
                col="red", 
                fill="blue", 
                #alpha = .2
        ) +  
        labs(title="Wartoœci wolumenu modelu z agentami
             cierpliwymi, niecierpliwymi i low-intelligence") +
        labs( x = "krok czasowy", y="wolumen")+
        theme(plot.title=element_text(size=14, face="bold"),
              axis.title=element_text(size=8))

```


```{r multiplotOfReturnsFM, echo=FALSE}
pdf('figure/multiplotOfHistogramsVolumeFMFMNNFMPlusNN.pdf')
multiplot(rFMggHist, rFMNNggHist, rFMPlusNNggHist, rows=3)
dev.off()
```
```{r multiplotOfReturnsFM, echo=FALSE}
pdf('figure/multiplotOfBoxPlotVolumeFMFMNNFMPlusNN.pdf')
multiplot(rFMggBoxPlot, rFMNNggBoxPlot, rFMPlusNNggBoxPlot, rows=3)
dev.off()
```

# 2. Opis danych wraz z b³êdem: œrednia, odchylenie, min, max; - Bootstrap

```{r}
# load boot and zoo packages
library(boot)        
library(zoo)   
# install.packages("stargazer")
# library(stargazer)

# rFM <- returnFM[, "returnCol"]
# vFM <- returnFM[, "volume"]
summary(rFM)
summary(vFM)

# rFMNN <- returnFM[, "returnColNN"]
# vFMNN <- returnFM[, "volumeNN"]
summary(rFMNN)
summary(vFMNN)

# rFMPlusNN <- returnFM[, "returnColPlusNN"]
# vFMPlusNN  <- returnFM[, "volumePlusNN"]
summary(rFMPlusNN)
summary(vFMPlusNN)

# eNN <- error[, "predictionErrorNN"]
# ePlus <- error[, "predictionErrorPlusNN"]
# summary(eNN)
# summary(ePlus)

# Podsumowanie danych 
summaryTable <- cbind(summary(rFM),
                      summary(rFMNN),
                      summary(rFMPlusNN),
                      summary(vFM),
                      summary(vFMNN),
                      summary(vFMPlusNN))
# 
# summaryTable <- cbind(summary(rFM),
#                       summary(rFMNN),
#                       summary(vFM),
#                       summary(vFMNN))

colnames(summaryTable) <- c("rFM", "rFMNN", "rFMPlusNN", "vFM", "vFMNN", 
                            "vFMPlusNN")
# colnames(summaryTable) <- c("rFM", "rFMNN", "vFM", "vFMNN")

# summaryTable 
round(summaryTable, digits=4)
```

```{r summaryTableReturnCode, include=FALSE}
summaryTableReturns <- cbind(
                      summary(rFM),
                      summary(rFMNN),
                      summary(rFMPlusNN)
                      )

# summaryTableReturns <- cbind(
#         summary(rFM),
#         summary(rFMNN))
# par(mar = c(3,3,3,3))
colnames(summaryTableReturns) <- c("rFM", "rFMNN",  "rFMPlusNN")
# colnames(summaryTableReturns) <- c("rFM", "rFMNN")
```

```{r summaryTableReturnsPlot, echo=FALSE}
round(summaryTableReturns, digits=4)
pdf('figure/summaryTableReturns.pdf')
mosaicplot(abs(summaryTableReturns), col=rainbow(3), 
           main="Wizualizacja wartoœci bezwzglêdnych danych z tabeli 
        podsumowuj¹cej wyniki stóp zwrotu modeli FM, FMNN, FMPlusNN")
dev.off()
```
```{r summaryTableVolumeCode, include=FALSE}
summaryTableVolume <- cbind(summary(vFM),
                      summary(vFMNN),
                      summary(vFMPlusNN)
                      )
colnames(summaryTableVolume) <- c("vFM", "vFMNN", "vFMPlusNN")
```

```{r summaryTableVolumePlot, echo=FALSE}
round(summaryTableVolume, digits=4)
pdf('figure/summaryTableVolume.pdf')
mosaicplot(summaryTableVolume, col=rainbow(3), 
        main="Wizualizacja danych tabeli podsumowuj¹cej wyniki wolumenu
           modeli FM, FMNN, FMPlusNN")
dev.off()
```
# 
# ```{r summaryTableErrorCode, include=FALSE}
# summaryTableError <- cbind(
#                       summary(eNN),
#                       summary(ePlus)
#                       )
# colnames(summaryTableError) <- c("eNN", "ePlus")
# ```
# 
# ```{r summaryTableErrorPlot, echo=FALSE}
# round(summaryTableError, digits=4)
# pdf('figure/summaryTableError.pdf')
# mosaicplot(summaryTableError, col=rainbow(2), 
#         main="Wizualizacja danych tabeli podsumowuj¹cej wyniki b³êdów predykcji
#            modeli FM, FMNN, FMPlusNN")
# dev.off()
# ```

# 
# ```{r}
# # Read S&P500
# # read prices from csv file on class webpage
# cerExample.df = read.csv(file="cerExample.csv")
# # create zooreg object - regularly spaced zoo object
# cerExample.z = zooreg(data=as.matrix(cerExample.df), start=c(1992,6), 
#                       end=c(2000,10), frequency=12)
# index(cerExample.z) = as.yearmon(index(cerExample.z))
# cerExample.z = as.zoo(cerExample.z)
# # compute returns
# returns.z = diff(log(cerExample.z))
# head(returns.z)
# returns.mat = coredata(returns.z)
# SP500 = returns.mat[,"sp500"]
# head(SP500)
# ```

# Oszacowanie œrednich wartoœci oraz odchyleñ standardowych stóp zwrotu. 
# wy³¹cznie autokorelacja.

```{r MuSigma 1/3, include=FALSE}
#rFM
muhatRFM = mean(rFM)
sigmahatRFM = sd(rFM)
# rhohatRFMAndSP500 = cor( returns.mat[,"sp500"], rFM[ 
#         (length(rFM)-length(returns.mat[,"sp500"])+1) : length(rFM) ] )
muhatRFM
sigmahatRFM
# rhohatRFMAndSP500
seMuhatRFM = sigmahatRFM/sqrt(length(rFM))
rbind(muhatRFM, seMuhatRFM)

#rFMNN       
muhatRFMNN = mean(rFMNN)
sigmahatRFMNN = sd(rFMNN)
# rhohatRFMNNAndSP500 = cor( returns.mat[,"sp500"], rFMNN[ 
#         (length(rFMNN)-length(returns.mat[,"sp500"])+1) : length(rFMNN) ] )
muhatRFMNN
sigmahatRFMNN
# rhohatRFMNNAndSP500
seMuhatRFMNN = sigmahatRFMNN/sqrt(length(rFMNN))
rbind(muhatRFMNN, seMuhatRFMNN)

#rFMPlusNN
muhatRFMPlusNN = mean(rFMPlusNN)
sigmahatRFMPlusNN = sd(rFMPlusNN)
# rhohatRFMPlusNNAndSP500 = cor( returns.mat[,"sp500"], rFMPlusNN[ 
#         (length(rFMPlusNN)-length(returns.mat[,"sp500"])+1) : 
#                 length(rFMPlusNN) ] )
muhatRFMPlusNN
sigmahatRFMPlusNN
# rhohatRFMPlusNNAndSP500
seMuhatRFMPlusNN = sigmahatRFMPlusNN/sqrt(length(rFMPlusNN))
rbind(muhatRFMPlusNN, seMuhatRFMPlusNN)
#summin up cor for returns
# cbind(rhohatRFMAndSP500, rhohatRFMNNAndSP500, rhohatRFMPlusNNAndSP500)

# vFM
muhatVFM = mean(vFM)
sigmahatVFM = sd(vFM)
muhatVFM
sigmahatVFM
seMuhatVFM = sigmahatVFM/sqrt(length(vFM))
rbind(muhatVFM, seMuhatVFM)

# vFMNN
muhatVFMNN = mean(vFMNN)
sigmahatVFMNN = sd(vFMNN)
muhatVFMNN
sigmahatVFMNN
seMuhatVFMNN = sigmahatVFMNN/sqrt(length(vFMNN))
rbind(muhatVFMNN, seMuhatVFMNN)

# vFMPlusNN
muhatVFMPlusNN = mean(vFMPlusNN)
sigmahatVFMPlusNN = sd(vFMPlusNN)
muhatVFMPlusNN
sigmahatVFMPlusNN
seMuhatVFMPlusNN = sigmahatVFMPlusNN/sqrt(length(vFMPlusNN))
rbind(muhatVFMPlusNN, seMuhatVFMPlusNN)

# # eNN
# muhatENN = mean(eNN)
# sigmahatENN = sd(eNN)
# muhatENN
# sigmahatENN
# seMuhatENN = sigmahatENN/sqrt(length(eNN))
# rbind(muhatENN, seMuhatENN)
# 
# # ePlus
# muhatEPlus = mean(ePlus)
# sigmahatEPlus = sd(ePlus)
# muhatEPlus
# sigmahatEPlus
# seMuhatEPlus = sigmahatEPlus/sqrt(length(vFMPlusNN))
# rbind(muhatEPlus, seMuhatEPlus)

sdTable <- cbind(muhatRFM,
                 sigmahatRFM,
                 muhatRFMNN,
                 sigmahatRFMNN,
                 muhatRFMPlusNN,
                 sigmahatRFMPlusNN,
                 muhatVFM,
                 sigmahatVFM,
                 muhatVFMNN,
                 sigmahatVFMNN,
                 muhatVFMPlusNN,
                 sigmahatVFMPlusNN
#                  muhatENN,
#                  sigmahatENN,
#                  muhatEPlus,
#                  sigmahatEPlus
                 )

# colnames(sdTable) <- c("rFM", "rFMNN", "rFMPlusNN", "vFM", "vFMNN", 
#                             "vFMPlusNN", "eNN", "ePlus")
# sdTable 
round(sdTable, digits=4)

sdTableReturnsMu <- cbind(muhatRFM,
                          muhatRFMNN,
                          muhatRFMPlusNN)
sdTableReturnsSigma <- cbind(
                        sigmahatRFM,
                        sigmahatRFMNN,
                        sigmahatRFMPlusNN)
sdTableReturns <- rbind(sdTableReturnsMu, sdTableReturnsSigma)
colnames(sdTableReturns) <- c("rFM", "rFMNN", "rFMPlusNN")
rownames(sdTableReturns) <- c("wartoœæ œrednia", "odchylenie standardowe")
```

```{r sdTableReturns, echo=FALSE}
round(sdTableReturns, digits=4)
```

```{r MuSigma 2/3, include=FALSE}
sdTableVolumeMu <- cbind(
                 muhatVFM,
                 muhatVFMNN,
                 muhatVFMPlusNN)
sdTableVolumeSd <- cbind(
                 sigmahatVFM,
                 sigmahatVFMNN,
                 sigmahatVFMPlusNN)
sdTableVolume <- rbind(sdTableVolumeMu, sdTableVolumeSd)
colnames(sdTableVolume) <- c("vFM", "vFMNN", "vFMPlusNN")
rownames(sdTableVolume) <- c("wartoœæ œrednia", "odchylenie standardowe")
```

```{r sdTableVolume, echo=FALSE}
round(sdTableVolume, digits=4)
```

# ```{r MuSigma 3/3, include=FALSE}
# sdTableErrorMu <- cbind(
#                         muhatENN,
#                         muhatEPlus)
# sdTableErrorSigma <- cbind(
#                         sigmahatENN,
#                         sigmahatEPlus)
# sdTableError <- rbind(sdTableErrorMu, sdTableErrorSigma)
# colnames(sdTableError) <- c("eNN", "ePlus")
# rownames(sdTableError) <- c("wartoœæ œrednia", "odchylenie standardowe")
# ```

# ```{r sdTableError, echo=FALSE}
# round(sdTableError, digits=4)
# ```

# ```{r correlationsSP500, include=FALSE}
# # correlations between vFMPlusNN
# rhohatVFMAndVFMNN = cor( vFM[ (length(vFM)-length(
#         returns.mat[,"sp500"])+1) : length(vFM) ], 
#         vFMNN[ (length(vFMNN)-length(returns.mat[,"sp500"])+1) : 
#                        length(vFMNN) ] )
# rhohatVFMAndVFMPlusNN = cor( vFM[ (length(vFM)-length(
#         returns.mat[,"sp500"])+1) : length(vFM) ], 
#         vFMPlusNN[ (length(vFMPlusNN)-length(returns.mat[,"sp500"])+1) : 
#                            length(vFMPlusNN) ] )
# rhohatVFMNNAndVFMPlusNN = cor( vFMNN[ (length(vFMNN)-length(
#         returns.mat[,"sp500"])+1) : length(vFMNN) ], 
#         vFMPlusNN[ (length(vFMPlusNN)-length(returns.mat[,"sp500"])+1) : 
#                            length(vFMPlusNN) ] )        
# 
# #summin up cor between volumes
# cbind(rhohatVFMAndVFMNN, rhohatVFMAndVFMPlusNN, rhohatVFMNNAndVFMPlusNN)
# 

# 
# # correlations between return and volume
# rhohatRetVol = cor( rFM[ (length(rFM)-length(returns.mat[,"sp500"])+1) : 
#                                  length(rFM) ], 
#                     vFM[ (length(vFM)-length(returns.mat[,"sp500"])+1) : 
#                                  length(vFM) ] )
# rhohatRetVolNN = cor( rFMNN[ (length(rFMNN)-length(returns.mat[,"sp500"])+1) : 
#                                      length(rFMNN) ], 
#                       vFMNN[ (length(vFMNN)-length(returns.mat[,"sp500"])+1) : 
#                                      length(vFMNN) ] )
# rhohatRetVolPlusNN = 
#         cor( rFMPlusNN[ (length(rFMPlusNN)-length(returns.mat[,"sp500"])+1) : 
#                                 length(rFMPlusNN) ], 
#              vFMPlusNN[ (length(vFMPlusNN)-
#                                  length(returns.mat[,"sp500"])+1) : 
#                                 length(vFMPlusNN) ] )
# cbind(rhohatRetVol, rhohatRetVolNN, rhohatRetVolPlusNN)
# 
```
```{r correlations, include=FALSE}
# correlations between return and volume
rhohatRetVol = cor( rFM, vFM )
rhohatRetVolNN = cor( rFMNN, vFMNN )
rhohatRetVolPlusNN = cor( rFMPlusNN, vFMPlusNN )
rhohat <- cbind(rhohatRetVol, rhohatRetVolNN, rhohatRetVolPlusNN)
colnames(rhohat) <- c("FMM", "FMMNN", "FMPusNN")
rownames(rhohat) <- "KorelacjaStopyZwortuOrazVolumnu"
```

```{r rhohat, echo=FALSE}
round(rhohat, digit=4)
```

# Wykorzystanie metody Bootstrap z bibliotek¹ boot

```{r MeanBoot, include=FALSE}
# function for bootstrapping sample mean
mean.boot = function(x, idx) {
        # arguments:
        # x                 data to be resampled
        # idx                vector of scrambled indices created by boot() function
        # value:
        # ans		mean value computed using resampled data
        ans = mean(x[idx])
        ans
}

# rFM
meanBootRFM = boot(rFM, statistic = mean.boot, R=999)
class(meanBootRFM)
names(meanBootRFM)

# print, plot and qqnorm methods
meanBootRFM
# compare boot SE with analytic SE
#seMuHatRFM1 = sd(rFM1)/sqrt(length(rFM1))
seMuhatRFM

# plot bootstrap distribution and qq-plot against normal
#plot(meanBootRFM)

# compute bootstrap confidence intervals from normal approximation
# basic bootstrap method and percentile intervals
mubootCIrFM <- boot.ci(meanBootRFM, conf = 0.95, type = c("norm","perc"))
mubootCIrFM

# compare boot confidence intervals with analytic confidence interval
rFMLower = mean(rFM)- 2*seMuhatRFM
rFMUpper = mean(rFM) + 2*seMuhatRFM
cbind(rFMLower,rFMUpper)

#rFMNN
meanBootRFMNN = boot(rFMNN, statistic = mean.boot, R=999)
class(meanBootRFMNN)
names(meanBootRFMNN)

# print, plot and qqnorm methods
meanBootRFMNN
# compare boot SE with analytic SE
#seMuHatRFM1 = sd(rFM1)/sqrt(length(rFM1))
seMuhatRFMNN

# plot bootstrap distribution and qq-plot against normal
#plot(meanBootRFMNN)

# compute bootstrap confidence intervals from normal approximation
# basic bootstrap method and percentile intervals
mubootCIrFMNN <- boot.ci(meanBootRFMNN, conf = 0.95, type = c("norm","perc"))
mubootCIrFMNN

# compare boot confidence intervals with analytic confidence interval
rFMNNLower = mean(rFMNN)- 2*seMuhatRFMNN
rFMNNUpper = mean(rFMNN) + 2*seMuhatRFMNN
cbind(rFMNNLower,rFMNNUpper)

#rFMPlusNN
meanBootRFMPlusNN = boot(rFMPlusNN, statistic = mean.boot, R=999)
class(meanBootRFMPlusNN)
names(meanBootRFMPlusNN)

# print, plot and qqnorm methods
meanBootRFMPlusNN
# compare boot SE with analytic SE
#seMuHatRFM1 = sd(rFM1)/sqrt(length(rFM1))
seMuhatRFMPlusNN

# plot bootstrap distribution and qq-plot against normal
#plot(meanBootRFMPlusNN)

# compute bootstrap confidence intervals from normal approximation
# basic bootstrap method and percentile intervals
mubootCIrFMPlusNN <- boot.ci(meanBootRFMPlusNN, conf = 0.95, 
                             type = c("norm","perc"))
mubootCIrFMPlusNN

# compare boot confidence intervals with analytic confidence interval
rFMPlusNNLower = mean(rFMPlusNN)- 2*seMuhatRFMPlusNN
rFMPlusNNUpper = mean(rFMPlusNN) + 2*seMuhatRFMPlusNN
cbind(rFMPlusNNLower,rFMPlusNNUpper)

# vFM
meanBootVFM = boot(vFM, statistic = mean.boot, R=999)
class(meanBootVFM)
names(meanBootVFM)

# print, plot and qqnorm methods
meanBootVFM
# compare boot SE with analytic SE
#seMuHatRFM1 = sd(rFM1)/sqrt(length(rFM1))
seMuhatVFM

# plot bootstrap distribution and qq-plot against normal
#plot(meanBootVFM)

# compute bootstrap confidence intervals from normal approximation
# basic bootstrap method and percentile intervals
mubootCIvFM <- boot.ci(meanBootVFM, conf = 0.95, type = c("norm","perc"))
mubootCIvFM

# compare boot confidence intervals with analytic confidence interval
vFMLower = mean(vFM)- 2*seMuhatVFM
vFMUpper = mean(vFM) + 2*seMuhatVFM
cbind(vFMLower,vFMUpper)

# vFMNN
meanBootVFMNN = boot(vFMNN, statistic = mean.boot, R=999)
class(meanBootVFMNN)
names(meanBootVFMNN)

# print, plot and qqnorm methods
meanBootVFMNN
# compare boot SE with analytic SE
#seMuHatRFM1 = sd(rFM1)/sqrt(length(rFM1))
seMuhatVFMNN

# plot bootstrap distribution and qq-plot against normal
#plot(meanBootVFMNN)

# compute bootstrap confidence intervals from normal approximation
# basic bootstrap method and percentile intervals
mubootCIvFMNN <- boot.ci(meanBootVFMNN, conf = 0.95, type = c("norm","perc"))
mubootCIvFMNN

# compare boot confidence intervals with analytic confidence interval
vFMNNLower = mean(vFMNN)- 2*seMuhatVFMNN
vFMNNUpper = mean(vFMNN) + 2*seMuhatVFMNN
cbind(vFMNNLower,vFMNNUpper)

# vFMPlusNN
meanBootVFMPlusNN = boot(vFMPlusNN, statistic = mean.boot, R=999)
class(meanBootVFMPlusNN)
names(meanBootVFMPlusNN)

# print, plot and qqnorm methods
meanBootVFMPlusNN
# compare boot SE with analytic SE
#seMuHatRFM1 = sd(rFM1)/sqrt(length(rFM1))
seMuhatVFMPlusNN

# plot bootstrap distribution and qq-plot against normal
#plot(meanBootVFMPlusNN)

# compute bootstrap confidence intervals from normal approximation
# basic bootstrap method and percentile intervals
mubootCIvFMPlusNN <- boot.ci(meanBootVFMPlusNN, conf = 0.95, 
                             type = c("norm","perc"))
mubootCIvFMPlusNN

# compare boot confidence intervals with analytic confidence interval
vFMPlusNNLower = mean(vFMPlusNN)- 2*seMuhatVFMPlusNN
vFMPlusNNUpper = mean(vFMPlusNN) + 2*seMuhatVFMPlusNN
cbind(vFMPlusNNLower,vFMPlusNNUpper)

# # eNN
# meanBootENN = boot(eNN, statistic = mean.boot, R=999)
# class(meanBootENN)
# names(meanBootENN)
# 
# # print, plot and qqnorm methods
# meanBootENN
# # compare boot SE with analytic SE
# #seMuHatRFM1 = sd(rFM1)/sqrt(length(rFM1))
# seMuhatENN
# 
# # plot bootstrap distribution and qq-plot against normal
# #plot(meanBootENN)
# 
# # compute bootstrap confidence intervals from normal approximation
# # basic bootstrap method and percentile intervals
# mubootCIeNN <- boot.ci(meanBootENN, conf = 0.95, type = c("norm","perc"))
# mubootCIeNN
# 
# # compare boot confidence intervals with analytic confidence interval
# eNNLower = mean(eNN)- 2*seMuhatENN
# eNNUpper = mean(eNN) + 2*seMuhatENN
# cbind(eNNLower,eNNUpper)
# 
# # ePlus
# meanBootEPlus = boot(ePlus, statistic = mean.boot, R=999)
# class(meanBootEPlus)
# names(meanBootEPlus)
# 
# # print, plot and qqnorm methods
# meanBootEPlus
# # compare boot SE with analytic SE
# #seMuHatRFM1 = sd(rFM1)/sqrt(length(rFM1))
# seMuhatEPlus
# 
# # plot bootstrap distribution and qq-plot against normal
# #plot(meanBootEPlus)
# 
# # compute bootstrap confidence intervals from normal approximation
# # basic bootstrap method and percentile intervals
# mubootCIePlus <- boot.ci(meanBootEPlus, conf = 0.95, type = c("norm","perc"))
# mubootCIePlus
# 
# # compare boot confidence intervals with analytic confidence interval
# ePlusLower = mean(ePlus)- 2*seMuhatEPlus
# ePlusUpper = mean(ePlus) + 2*seMuhatEPlus
# cbind(ePlusLower,ePlusUpper)
```

```{r Sd Boot, include=FALSE}
# function for bootstrapping sample standard deviation

# rFM1
sd.boot = function(x, idx) {
        # arguments:
        # x                 data to be resampled
        # idx        	vector of scrambled indices created by boot() function
        # value:
        # ans		sd value computed using resampled data
        ans = sd(x[idx])
        ans
}

#rFM
sdBootRFM = boot(rFM, statistic = sd.boot, R=999)
sdBootRFM

# compare boot SE with analytic SE
seSigmahatRFM = sigmahatRFM/sqrt(2*length(rFM))
sigmahatRFM
seSigmahatRFM

# plot bootstrap distribution
#plot(sdBootRFM)

# compute confidence intervals
sdBootCIrFM <- boot.ci(sdBootRFM, conf=0.95, type=c("norm", "basic", "perc"))
sdBootCIrFM

# compare boot confidence intervals with analytic confidence interval
sdRFMLower = sigmahatRFM - 2*seSigmahatRFM
sdRFMUpper = sigmahatRFM+ 2*seSigmahatRFM
cbind(sdRFMLower,sdRFMUpper)

#rFMNN
sdBootRFMNN = boot(rFMNN, statistic = sd.boot, R=999)
sdBootRFMNN

# compare boot SE with analytic SE
seSigmahatRFMNN = sigmahatRFMNN/sqrt(2*length(rFMNN))
sigmahatRFMNN
seSigmahatRFMNN

# plot bootstrap distribution
#plot(sdBootRFMNN)

# compute confidence intervals
sdBootCIrFMNN <- boot.ci(sdBootRFMNN, conf=0.95, type=c("norm", "basic", "perc"))
sdBootCIrFMNN

# compare boot confidence intervals with analytic confidence interval
sdRFMNNLower = sigmahatRFMNN - 2*seSigmahatRFMNN
sdRFMNNUpper = sigmahatRFMNN + 2*seSigmahatRFMNN
cbind(sdRFMNNLower,sdRFMNNUpper)

#rFMPlusNN
sdBootRFMPlusNN = boot(rFMPlusNN, statistic = sd.boot, R=999)
sdBootRFMPlusNN

# compare boot SE with analytic SE
seSigmahatRFMPlusNN = sigmahatRFMPlusNN/sqrt(2*length(rFMPlusNN))
sigmahatRFMPlusNN
seSigmahatRFMPlusNN

# plot bootstrap distribution
#plot(sdBootRFMPlusNN)

# compute confidence intervals
sdBootCIrFMPlusNN <- boot.ci(sdBootRFMPlusNN, conf=0.95, type=c("norm", "basic", "perc"))
sdBootCIrFMPlusNN

# compare boot confidence intervals with analytic confidence interval
sdRFMPlusNNLower = sigmahatRFMPlusNN - 2*seSigmahatRFMPlusNN
sdRFMPlusNNUpper = sigmahatRFMPlusNN + 2*seSigmahatRFMPlusNN
cbind(sdRFMPlusNNLower,sdRFMPlusNNUpper)

#vFM
sdBootVFM = boot(vFM, statistic = sd.boot, R=999)
sdBootVFM

# compare boot SE with analytic SE
seSigmahatVFM = sigmahatVFM/sqrt(2*length(vFM))
sigmahatVFM
seSigmahatVFM

# plot bootstrap distribution
#plot(sdBootVFM)

# compute confidence intervals
sdBootCIvFM <- boot.ci(sdBootVFM, conf=0.95, type=c("norm", "basic", "perc"))
sdBootCIvFM

# compare boot confidence intervals with analytic confidence interval
sdVFMLower = sigmahatVFM - 2*seSigmahatVFM
sdVFMUpper = sigmahatVFM+ 2*seSigmahatVFM
cbind(sdVFMLower,sdVFMUpper)

#vFMNN
sdBootVFMNN = boot(vFMNN, statistic = sd.boot, R=999)
sdBootVFMNN

# compare boot SE with analytic SE
seSigmahatVFMNN = sigmahatVFMNN/sqrt(2*length(vFMNN))
sigmahatVFMNN
seSigmahatVFMNN

# plot bootstrap distribution
#plot(sdBootVFMNN)

# compute confidence intervals
sdBootCIvFMNN <- boot.ci(sdBootVFMNN, conf=0.95, type=c("norm", "basic", "perc"))
sdBootCIvFMNN

# compare boot confidence intervals with analytic confidence interval
sdVFMNNLower = sigmahatVFMNN - 2*seSigmahatVFMNN
sdVFMNNUpper = sigmahatVFMNN+ 2*seSigmahatVFMNN
cbind(sdVFMNNLower,sdVFMNNUpper)

#vFMPlusNN
sdBootVFMPlusNN = boot(vFMPlusNN, statistic = sd.boot, R=999)
sdBootVFMPlusNN

# compare boot SE with analytic SE
seSigmahatVFMPlusNN = sigmahatVFMPlusNN/sqrt(2*length(vFMPlusNN))
sigmahatVFMPlusNN
seSigmahatVFMPlusNN

# plot bootstrap distribution
#plot(sdBootVFMPlusNN)

# compute confidence intervals
sdBootCIvFMPlusNN <- boot.ci(sdBootVFMPlusNN, conf=0.95, type=c("norm", "basic", "perc"))

# compare boot confidence intervals with analytic confidence interval
sdVFMPlusNNLower = sigmahatVFMPlusNN - 2*seSigmahatVFMPlusNN
sdVFMPlusNNUpper = sigmahatVFMPlusNN+ 2*seSigmahatVFMPlusNN
cbind(sdVFMPlusNNLower,sdVFMPlusNNUpper)

# #eNN
# sdBootENN = boot(eNN, statistic = sd.boot, R=999)
# sdBootENN
# 
# # compare boot SE with analytic SE
# seSigmahatENN = sigmahatENN/sqrt(2*length(eNN))
# sigmahatENN
# seSigmahatENN
# 
# # plot bootstrap distribution
# #plot(sdBootENN)
# 
# # compute confidence intervals
# sdBootCIeNN <- boot.ci(sdBootENN, conf=0.95, type=c("norm", "basic", "perc"))
# 
# # compare boot confidence intervals with analytic confidence interval
# sdENNLower = sigmahatENN - 2*seSigmahatENN
# sdENNUpper = sigmahatENN + 2*seSigmahatENN
# cbind(sdENNLower,sdENNUpper)
# 
# #ePlus
# sdBootEPlus = boot(ePlus, statistic = sd.boot, R=999)
# sdBootEPlus
# 
# # compare boot SE with analytic SE
# seSigmahatEPlus = sigmahatEPlus/sqrt(2*length(ePlus))
# sigmahatEPlus
# seSigmahatEPlus
# 
# # plot bootstrap distribution
# #plot(sdBootEPlus)
# 
# # compute confidence intervals
# sdBootCIePlus <- boot.ci(sdBootEPlus, conf=0.95, 
#                          type=c("norm", "basic", "perc"))
# sdBootCIePlus
# 
# # compare boot confidence intervals with analytic confidence interval
# sdEPlusLower = sigmahatEPlus - 2*seSigmahatEPlus
# sdEPlusUpper = sigmahatEPlus + 2*seSigmahatEPlus
# cbind(sdEPlusLower,sdEPlusUpper)

# summing up boot data
# bias: mean(meanBootRFM$t) - meanBootRFM$t0
# original statistic (example: mean) meanBootRFM$t0
# std. error: sd(meanBootRFM$t)

statFrommeanBootRFM <- c(mean(meanBootRFM$t), 
                        sd(meanBootRFM$t),
                        mean(meanBootRFM$t) - meanBootRFM$t0)
statFromsdBootRFM <- c(mean(sdBootRFM$t), 
                      sd(sdBootRFM$t),
                      mean(sdBootRFM$t) - sdBootRFM$t0)
statFrommeanBootRFMNN <- c(mean(meanBootRFMNN$t), 
                          sd(meanBootRFMNN$t),
                          mean(meanBootRFMNN$t) - meanBootRFMNN$t0)
statFromsdBootRFMNN <- c(mean(sdBootRFMNN$t), 
                        sd(sdBootRFMNN$t),
                        mean(sdBootRFMNN$t) - sdBootRFMNN$t0)
statFrommeanBootRFMPlusNN <- c(mean(meanBootRFMPlusNN$t), 
                              sd(meanBootRFMPlusNN$t),
                              mean(meanBootRFMPlusNN$t) - meanBootRFMPlusNN$t0)
statFromsdBootRFMPlusNN <- c(mean(sdBootRFMPlusNN$t), 
                             sd(sdBootRFMPlusNN$t),
                             mean(sdBootRFMPlusNN$t) - sdBootRFMPlusNN$t0)
statFrommeanBootVFM <- c(mean(meanBootVFM$t), 
                         sd(meanBootVFM$t),
                         mean(meanBootVFM$t) - meanBootVFM$t0)
statFromsdBootVFM <- c(mean(sdBootVFM$t), 
                       sd(sdBootVFM$t),
                       mean(sdBootVFM$t) - sdBootVFM$t0)
statFrommeanBootVFMNN <- c(mean(meanBootVFMNN$t), 
                           sd(meanBootVFMNN$t),
                           mean(meanBootVFMNN$t) - meanBootVFMNN$t0)
statFromsdBootVFMNN <- c(mean(sdBootVFMNN$t), 
                         sd(sdBootVFMNN$t),
                         mean(sdBootVFMNN$t) - sdBootVFMNN$t0)
statFrommeanBootVFMPlusNN <- c(mean(meanBootVFMPlusNN$t), 
                              sd(meanBootVFMPlusNN$t),
                              mean(meanBootVFMPlusNN$t) - meanBootVFMPlusNN$t0)
statFromsdBootVFMPlusNN <- c(mean(sdBootVFMPlusNN$t), 
                             sd(sdBootVFMPlusNN$t),
                             mean(sdBootVFMPlusNN$t) - sdBootVFMPlusNN$t0)
# statFrommeanBootENN <- c(mean(meanBootENN$t), 
#                          sd(meanBootENN$t),
#                          mean(meanBootENN$t) - meanBootENN$t0)
# statFromsdBootENN <- c(mean(sdBootENN$t), 
#                        sd(sdBootENN$t),
#                        mean(sdBootENN$t) - sdBootENN$t0)
# statFrommeanBootEPlus <- c(mean(meanBootEPlus$t), 
#                            sd(meanBootEPlus$t),
#                            mean(meanBootEPlus$t) - meanBootEPlus$t0)
# statFromsdBootEPlus <- c(mean(sdBootEPlus$t), 
#                          sd(sdBootEPlus$t),
#                          mean(sdBootEPlus$t) - sdBootEPlus$t0)

statFromBootRFM <- c(statFrommeanBootRFM, statFromsdBootRFM) 
statFromBootRFMNN <- c(statFrommeanBootRFMNN, statFromsdBootRFMNN) 
statFromBootRFMPlusNN <- c(statFrommeanBootRFMPlusNN, 
                                statFromsdBootRFMPlusNN)
statFromBootVFM <- c(statFrommeanBootVFM, statFromsdBootVFM)
statFromBootVFMNN <- c(statFrommeanBootVFMNN, statFromsdBootVFMNN) 
statFromBootVFMPlusNN <- c(statFrommeanBootVFMPlusNN, 
                                statFromsdBootVFMPlusNN) 
# statFromBootENN <- c(statFrommeanBootENN, statFromsdBootENN) 
# statFromBootEPlus <- c(statFrommeanBootEPlus, statFromsdBootEPlus) 

statBootRVE <- rbind(statFromBootRFM, statFromBootRFMNN, statFromBootRFMPlusNN, 
                statFromBootVFM, statFromBootVFMNN, statFromBootVFMPlusNN
#                 statFromBootENN, statFromBootEPlus
                )

colnames(statBootRVE) <- c("œr.", "b³.st.œr.", "bias", 
                         "odch.st.", "b³.st.odch.", "bias") 
rownames(statBootRVE) <- c("rFM",  "rFMNN", "rFMPlusNN", 
                           "vFM",  "vFMNN", "vFMPlusNN"
#                            "eFMNN", "eFMPlusNN"
                           )
```

```{r statBootRVE, echo=FALSE}
round(statBootRVE, digits=4)
```

```{r bootCI, include=FALSE}
bootCIrFM <- rbind(
c(mean(meanBootRFM$t), mubootCIrFM$normal[2:3], mubootCIrFM$percent[,4:5]),
c(sd(sdBootRFM$t), sdBootCIrFM$normal[2:3], sdBootCIrFM$percent[,4:5]))
bootCIrFMNN <- rbind(
c(mean(meanBootRFMNN$t), mubootCIrFMNN$normal[2:3], 
  mubootCIrFMNN$percent[,4:5]),
c(sd(sdBootRFMNN$t), sdBootCIrFMNN$normal[2:3], sdBootCIrFMNN$percent[,4:5]))
bootCIrFMPlusNN <- rbind(
c(mean(meanBootRFMPlusNN$t), mubootCIrFMPlusNN$normal[2:3], 
  mubootCIrFMPlusNN$percent[,4:5]),
c(sd(sdBootRFMPlusNN$t), sdBootCIrFMPlusNN$normal[2:3], 
  sdBootCIrFMPlusNN$percent[,4:5]))
bootCIvFM <- rbind(
c(mean(meanBootVFM$t), mubootCIvFM$normal[2:3], mubootCIvFM$percent[,4:5]),
c(sd(sdBootVFM$t), sdBootCIvFM$normal[2:3], sdBootCIvFM$percent[,4:5]))
bootCIvFMNN <- rbind(
c(mean(meanBootVFMNN$t), mubootCIvFMNN$normal[2:3], 
  mubootCIvFMNN$percent[,4:5]),
c(sd(sdBootVFMNN$t), sdBootCIvFMNN$normal[2:3], sdBootCIvFMNN$percent[,4:5]))
bootCIvFMPlusNN <- rbind(
c(mean(meanBootVFMPlusNN$t), mubootCIvFMPlusNN$normal[2:3], 
  mubootCIvFMPlusNN$percent[,4:5]),
c(sd(sdBootVFMPlusNN$t), sdBootCIvFMPlusNN$normal[2:3], 
  sdBootCIvFMPlusNN$percent[,4:5]))
# bootCIeNN <- rbind(
#         c(mean(meanBootENN$t), mubootCIeNN$normal[2:3], 
#           mubootCIeNN$percent[,4:5]),
#         c(sd(sdBootVFMNN$t), sdBootCIeNN$normal[2:3], 
#           sdBootCIeNN$percent[,4:5]))
# bootCIePlus <- rbind(
#         c(mean(meanBootEPlus$t), mubootCIePlus$normal[2:3], 
#           mubootCIePlus$percent[,4:5]),
#         c(sd(sdBootEPlus$t), sdBootCIePlus$normal[2:3], 
#           sdBootCIePlus$percent[,4:5]))

statBootCiAll <- rbind(
        bootCIrFM ,bootCIrFMNN, bootCIrFMPlusNN, bootCIvFM, bootCIvFMNN, 
        bootCIvFMPlusNN
#         bootCIeNN, bootCIePlus
        )
# print(statBootCiAll, digits = 3)
# format(round(statBootCiAll, 3))
# fixed(statBootCiAll,digits=2)


colnames(statBootCiAll) <- c("w.Stat", "n.0.95dó³", "n.0.95góra", 
                             "kwant.0.025", "kwant.0.975")
rownames(statBootCiAll) <- c("œr.rFM", "odch.st.rFM",
                             "œr.rFMNN", "odch.st.rFMNN",
                             "œr.rFMPlusNN", "odch.st.rFMPlusNN",
                             "œr.vFM", "odch.st.vFM",
                             "œr.vFMNN", "odch.st.vFMNN",
                             "œr.vFMPlusNN", "odch.st.vFMPlusNN"
#                              "œr.eNN", "odch.st.eNN",
#                              "œr.ePlus", "odch.st.ePlus"
                             )
```

```{r statBootCiAll, echo=FALSE}
round(statBootCiAll, digits=4)
```

```{r AnaliticalMethod, include=FALSE}
# summing up analitical method
sumAnaliticalMethod <- rbind(
        c(muhatRFM, rFMLower,rFMUpper), 
        c(sigmahatRFM, sdRFMLower, sdRFMUpper),
        c(muhatRFMNN, rFMNNLower, rFMNNUpper),
        c(sigmahatRFMNN, sdRFMNNLower, sdRFMNNUpper),
        c(muhatRFMPlusNN, rFMPlusNNLower, rFMPlusNNUpper),
        c(sigmahatRFMPlusNN, sdRFMPlusNNLower,sdRFMPlusNNUpper), 
        c(muhatVFM, vFMLower, vFMUpper),
        c(sigmahatVFM, sdVFMLower, sdVFMUpper),
        c(muhatVFMNN, vFMNNLower, vFMNNUpper),
        c(sigmahatVFMNN, sdVFMNNLower, sdVFMNNUpper),
        c(muhatVFMPlusNN, vFMPlusNNLower, vFMPlusNNUpper), 
        c(sigmahatVFMPlusNN, sdVFMPlusNNLower, sdVFMPlusNNUpper)
#         c(muhatENN, eNNLower, eNNUpper),
#         c(sigmahatENN, sdENNLower, sdENNUpper),
#         c(muhatEPlus, ePlusLower, ePlusUpper), 
#         c(sigmahatEPlus, sdEPlusLower, sdEPlusUpper)
        )
colnames(sumAnaliticalMethod) <- c("w.Stat", "n.0.95dó³", "n.0.95góra")
rownames(sumAnaliticalMethod) <- c("œr.rFM", "odch.st.rFM",
                             "œr.rFMNN", "odch.st.rFMNN",
                             "œr.rFMPlusNN", "odch.st.rFMPlusNN",
                             "œr.vFM", "odch.st.vFM",
                             "œr.vFMNN", "odch.st.vFMNN",
                             "œr.vFMPlusNN", "odch.st.vFMPlusNN"
#                              "œr.eNN", "odch.st.eNN",
#                              "œr.ePlus", "odch.st.ePlus"
                             )
```

```{r sumAnaliticalMethod, echo=FALSE}
round(sumAnaliticalMethod, digits=4)
```

# ```{r bootPlot, echo=FALSE}]
# pdf('figure/meanBootRFM.pdf')
# plot(meanBootRFM)
# dev.off()
# pdf('figure/meanBootRFMNN.pdf')
# plot(meanBootRFMNN)
# dev.off()
# pdf('figure/meanBootRFMPlusNN.pdf')
# plot(meanBootRFMPlusNN)
# dev.off()
# pdf('figure/meanBootVFM.pdf')
# plot(meanBootVFM)
# dev.off()
# pdf('figure/meanBootVFMNN.pdf')
# plot(meanBootVFMNN)
# dev.off()
# pdf('figure/meanBootVFMPlusNN.pdf')
# plot(meanBootVFMPlusNN)
# dev.off()
# # pdf('figure/meanBootENN.pdf')
# # plot(meanBootENN)
# # dev.off()
# # pdf('figure/meanBootEPlus.pdf')
# # plot(meanBootEPlus)
# # dev.off()
# 
# pdf('figure/sdBootRFM.pdf')
# plot(sdBootRFM)
# dev.off()
# pdf('figure/sdBootRFMNN.pdf')
# plot(sdBootRFMNN)
# dev.off()
# pdf('figure/sdBootRFMPlusNN.pdf')
# plot(sdBootRFMPlusNN)
# dev.off()
# pdf('figure/sdBootVFM.pdf')
# plot(sdBootVFM)
# dev.off()
# pdf('figure/sdBootVFMNN.pdf')
# plot(sdBootVFMNN)
# dev.off()
# pdf('figure/sdBootVFMPlusNN.pdf')
# plot(sdBootVFMPlusNN)
# dev.off()
# # pdf('figure/sdBootENN.pdf')
# # plot(sdBootENN)
# # dev.off()
# # pdf('figure/sdBootEPlus.pdf')
# # plot(sdBootEPlus)
# # dev.off()
# ```

# 3. Wizualizacja danych.
```{r rFMggPlotCode, include=FALSE}
library("PerformanceAnalytics")
# library("mvtnorm")
# library("ggplot2")

# par("mar")
# par(mar = c(2, 2, 2, 2))
# par(mfrow=c(3,1))
# plot(rFM,ylab="stopa zwrotu",xlab = "krok czasowy",
#      main="Stopy zwrotu z symulacji modelu z agentami 
#         cierpliwymi i niecierpliwymi",type = "line", 
#      col="blue", lwd=1)
# abline(h=0)
# plot(rFMNN,ylab="stopa zwrotu",xlab = "krok czasowy",
#      main="Stopy zwrotu z symulacji modelu z agentami 
#         cierpliwymi i low-intelligence", type = "line",
#      col="blue", lwd=1)
# abline(h=0)
# plot(rFMPlusNN,ylab="stopa zwrotu",
#      main="Stopy zwrotu z symulacji modelu z agentami 
#         cierpliwymi, niecierpliwymi i low-intelligence", type = "line",
#      col="blue", lwd=1)
# abline(h=0)
# par(mfrow=c(1,1))
# dev.off()
# 
# par(mar = c(2, 2, 2, 2))
# par(mfrow=c(3,1))
# plot(vFM,ylab="wolumen", xlab = "krok czasowy",
#      main="Wolumen z symulacji modelu z agentami 
#         cierpliwymi i niecierpliwymi",
#      col="blue", lwd=1)
# abline(h=0)
# plot(vFMNN,ylab="wolumen", xlab = "krok czasowy",
#      main="Wolumen z symulacji modelu z agentami 
#         cierpliwymi i low-intelligence",
#      col="blue", lwd=1)
# abline(h=0)
# plot(vFMPlusNN,ylab="wolumen", xlab = "krok czasowy",
#      main="Wolumen z symulacji modelu z agentami 
#         cierpliwymi, niecierpliwymi i low-intelligence",
#      col="blue", lwd=1)
# abline(h=0)
# par(mfrow=c(1,1))
# dev.off()
# 
# #ggPlot of rFM
# rFMDataFrame <- as.data.frame(rFM)
# rFMDataFrame$step <- c(1:length(rFM))
# rFMggPlot <- ggplot(data = rFMDataFrame, aes(x = step , y = rFM))
# rFMggPlot <- rFMggPlot + geom_line(colour = "red")
# rFMggPlot <- rFMggPlot + geom_abline(colour = "blue", slope = 0)
# rFMggPlot <- rFMggPlot + ggtitle("Stopy zwrotu z symulacji modelu z agentami 
# cierpliwymi i niecierpliwymi") + 
#                         xlab("krok czasowy") + 
#                         ylab("stopa zwrotu") +
#                         theme(plot.title=element_text(size=8, face="bold", 
#                                                       hjust = 0.5), 
#                                 axis.title=element_text(size=8))
# #         scale_y_continuous(breaks=seq(-.9, 1.2, 0.1))
# ```
# 
# ```{r rFMggPlot, echo=FALSE}
# pdf('figure/rFMggPlot.pdf')
# rFMggPlot
# dev.off()        
# ```
# 
# ```{r rFMNNggPlotCode, include=FALSE}
# # ggsave("rFMggPlot.png", plot = rFMggPlot, width = 4, height = 4)
# 
# # ggPlot of rFMNN
# rFMNNDataFrame <- as.data.frame(rFMNN)
# rFMNNDataFrame$step <- c(1:length(rFMNN))
# rFMNNggPlot <- ggplot(data = rFMNNDataFrame, aes(x = step , y = rFMNN))
# rFMNNggPlot <- rFMNNggPlot + geom_line(colour = "red")
# rFMNNggPlot <- rFMNNggPlot + geom_abline(colour = "blue", slope = 0)
# rFMNNggPlot <- rFMNNggPlot + ggtitle("Stopy zwrotu z symulacji modelu z agentami 
# cierpliwymi i low-intelligence") + 
#         xlab("krok czasowy") + 
#         ylab("stopa zwrotu") +
#         theme(plot.title=element_text(size=8, face="bold", hjust=0.5), 
#               axis.title=element_text(size=8))
# #         scale_y_continuous(breaks=seq(-.9, 1.2, 0.1))
# ```
# 
# ```{r rFMNNggPlot, eco=FALSE}
# pdf('figure/rFMNNggPlot.pdf')
# rFMNNggPlot
# dev.off()  
# ```
# 
# ```{r rFMPlusNNggPlotCode, include=FALSE}
# # ggsave("rFMNNggPlot.png", plot = rFMNNggPlot, width = 4, height = 4)
# 
# # ggPlot of rFMPlusNN
# rFMPlusNNDataFrame <- as.data.frame(rFMPlusNN)
# rFMPlusNNDataFrame$step <- c(1:length(rFMPlusNN))
# rFMPlusNNggPlot <- ggplot(data = rFMPlusNNDataFrame, aes(x = step , 
#                                                          y = rFMPlusNN))
# rFMPlusNNggPlot <- rFMPlusNNggPlot + geom_line(colour = "red")
# rFMPlusNNggPlot <- rFMPlusNNggPlot + geom_abline(colour = "blue", slope = 0)
# rFMPlusNNggPlot <- rFMPlusNNggPlot + 
#                         ggtitle("Stopy zwrotu z symulacji modelu z agentami 
# cierpliwymi i niecierpliwymi") + 
#         xlab("krok czasowy") + 
#         ylab("stopa zwrotu") +
#         theme(plot.title=element_text(size=8, face="bold", hjust=0.5),
#               axis.title=element_text(size=8))
# #         scale_y_continuous(breaks=seq(-10, 2.5, 0.5))
# ```
# 
# ```{r rFMPlusNNggPlot, eco=FALSE}
# pdf('figure/rFMPlusNNggPlot.pdf')
# rFMPlusNNggPlot
# dev.off()  
# ```
# 
# ```{r vFMggPlotCode, include=FALSE}
# # ggsave("rFMPlusNNggPlot.png", plot = rFMPlusNNggPlot, width = 4, height = 4)
# 
# #ggPlot of vFM
# vFMDataFrame <- as.data.frame(vFM)
# vFMDataFrame$step <- c(1:length(vFM))
# vFMggPlot <- ggplot(data = vFMDataFrame, aes(x = step , y = vFM))
# vFMggPlot <- vFMggPlot + geom_line(colour = "red")
# vFMggPlot <- vFMggPlot + ggtitle("Wolumen z symulacji modelu z agentami 
# cierpliwymi i niecierpliwymi") + 
#         xlab("krok czasowy") + 
#         ylab("Wolumen") +
#         theme(plot.title=element_text(size=14, face="bold"),
#               axis.title=element_text(size=8))
#         #scale_y_continuous(breaks=seq(-.9, 1.2, 0.1))
#         #+ scale_y_continuous(limits = 
#           #      c( min(vFMDataFrame$vFM), max(vFMDataFrame$vFM) ) )
# ```
# 
# ```{r vFMggPlot, echo=FALSE}
# pdf('figure/vFMggPlot.pdf')
# vFMggPlot
# dev.off()
# ```
# 
# ```{r vFMNNggPlotCode, include=FALSE}
# # ggsave("vFMggPlot.png", plot = vFMggPlot, width = 4, height = 4)
# 
# #ggPlot of vFMNN
# vFMNNDataFrame <- as.data.frame(vFMNN)
# vFMNNDataFrame$step <- c(1:length(vFMNN))
# vFMNNggPlot <- ggplot(data = vFMNNDataFrame, aes(x = step , y = vFMNN))
# vFMNNggPlot <- vFMNNggPlot + geom_line(colour = "red")
# vFMNNggPlot <- vFMNNggPlot + ggtitle("Wolumen z symulacji modelu z agentami 
# cierpliwymi i low-intelligence") + 
#         xlab("krok czasowy") + 
#         ylab("Wolumen") +
#         theme(plot.title=element_text(size=14, face="bold"),
#               axis.title=element_text(size=8))
# #scale_y_continuous(breaks=seq(-.9, 1.2, 0.1))
# #+ scale_y_continuous(limits = 
# #      c( min(vFMDataFrame$vFM), max(vFMDataFrame$vFM) ) )
# ```
# 
# ```{r vFMNNggPlot, echo=FALSE}
# pdf('figure/vFMNNggPlot.pdf')
# vFMNNggPlot
# dev.off()
# ```
# 
# ```{r vFMPlusNNggPlotCode, include=FALSE}
# # ggsave("vFMNNggPlot.png", plot = vFMNNggPlot, width = 4, height = 4)
# 
# #ggPlot of vFMPlusNN
# vFMPlusNNDataFrame <- as.data.frame(vFMPlusNN)
# vFMPlusNNDataFrame$step <- c(1:length(vFMPlusNN))
# vFMPlusNNggPlot <- ggplot(data = vFMPlusNNDataFrame, 
#                           aes(x = step , y = vFMPlusNN))
# vFMPlusNNggPlot <- vFMPlusNNggPlot + geom_line(colour = "red")
# vFMPlusNNggPlot <- vFMPlusNNggPlot + 
#                         ggtitle("Wolumen z symulacji modelu z agentami 
# cierpliwymi, niecierpliwymi i 
# low-intelligence") + 
#         xlab("krok czasowy") + 
#         ylab("Wolumen")+
#         theme(plot.title=element_text(size=14, face="bold"),
#               axis.title=element_text(size=8))
# #scale_y_continuous(breaks=seq(-.9, 1.2, 0.1))
# #+ scale_y_continuous(limits = 
# #      c( min(vFMDataFrame$vFM), max(vFMDataFrame$vFM) ) )
# ```
# 
# ```{r vFMPlusNNggPlot, echo=FALSE}
# pdf('figure/vFMPlusNNggPlot.pdf')
# vFMPlusNNggPlot
# dev.off()
# ```

```{r multiplotCode, include=FALSE}
# ggsave("vFMPlusNNggPlot.png", plot = vFMPlusNNggPlot, width = 4, height = 4)
# 
# library(grid)
# # multiplot function has been created by Winston Chang and the cod has been
# # copied from: 
# # http://www.cookbook-r.com/Graphs/Multiple_graphs_on_one_page_(ggplot2)/
# multiplot <- function(..., plotlist=NULL, file, cols=1, layout=NULL) {
#         require(grid)
#         
#         # Make a list from the ... arguments and plotlist
#         plots <- c(list(...), plotlist)
#         
#         numPlots = length(plots)
#         
#         # If layout is NULL, then use 'cols' to determine layout
#         if (is.null(layout)) {
#                 # Make the panel
#                 # ncol: Number of columns of plots
#                 # nrow: Number of rows needed, calculated from # of cols
#                 layout <- matrix(seq(1, cols * ceiling(numPlots/cols)),
#                                  ncol = cols, nrow = ceiling(numPlots/cols))
#         }
#         
#         if (numPlots==1) {
#                 print(plots[[1]])
#                 
#         } else {
#                 # Set up the page
#                 grid.newpage()
#                 pushViewport(viewport(layout = grid.layout(nrow(layout), 
#                                                            ncol(layout))))
#                 
#                 # Make each plot, in the correct location
#                 for (i in 1:numPlots) {
#                         # Get the i,j matrix positions of the regions that 
#                         # contain this subplot
#                         matchidx <- as.data.frame(which(layout == i, 
#                                                         arr.ind = TRUE))
#                         
#                         print(plots[[i]], vp = viewport
#                               (layout.pos.row = matchidx$row,
#                                layout.pos.col = matchidx$col))
#                 }
#         }
# }

# rFM
# muhatRFM
# sigmahatRFM
# 
# par(mfrow=c(1,2))
# plot(rFM,ylab="returns",
#      main="",
#      col="blue", lwd=2)
# abline(h=0)
# hist(rFM, main="", xlab="returns", col="slateblue1")
# par(mfrow=c(1,1))
# 
# par(mfrow=c(2,2))
# hist(rFM, xlab="return",ylab="frequency", 
#      main="cc returns from FM with Patient and Impatient Agent", 
#      col="slateblue1")
# boxplot(rFM, col="slateblue1")
# plot(density(rFM,type="l",xlab="return",ylab="density",
#              lwd=2, col="slateblue1", main="smoothed densiy"))
# qqnorm(rFM, col="blue")
# qqline(rFM)
# par(mfrow=c(1,1))

# rFMggHist <- ggplot(data=rFMDataFrame, aes(rFMDataFrame$rFM)) + 
#                         geom_histogram(aes(y =..density..), 
#                                 #breaks=seq(20, 50, by = 2), 
#                                 col="red", 
#                                 fill="blue", 
#                                 #alpha = .2
#                                 ) + 
#                 geom_density(col=2) + 
#                 labs(title="Rozk³ad stóp zwrotu modelu z agentami
# cierpliwymi i niecierpliwymi") +
#                 labs(x="Wartoœæ stopy zwrotu", y="Czêstotliwoœæ wystêpowania",
#                      size=8)+
#         theme(plot.title=element_text(size=8, face="bold"),
#               axis.title=element_text(size=8))
# 
# rFMggBoxPlot <- ggplot(data=rFMDataFrame, aes(x = rFMDataFrame$step, 
#                                               y = rFMDataFrame$rFM)) + 
#         geom_boxplot( 
#         #breaks=seq(20, 50, by = 2), 
#         col="red", 
#         fill="blue", 
#         #alpha = .2
#         ) +  
#         labs(title="Wartoœci stóp zwrotu modelu 
# z agentami cierpliwymi i niecierpliwymi") +
#         labs( x = "krok czasowy", y="stopa zwrotu", size=8)+
#         theme(plot.title=element_text(size=8, face="bold", hjust = 0.5),
#               axis.title=element_text(size=8))
# 
# # rFMggPlot
# # rFMggHist
# # rFMggBoxPlot
# ```
# 
# ```{r multiplotOfReturnsFM, echo=FALSE}
# pdf('figure/multiplotOfReturnsFM.pdf')
# multiplot(rFMggPlot, rFMggHist, rFMggBoxPlot, rows=3)
# dev.off()
# ```

```{r multiplotOfReturnsFMCode, include=FALSE}
# # rFMNN
# muhatRFMNN
# sigmahatRFMNN
# 
# par(mfrow=c(1,2))
# plot(rFMNN,ylab="returns",
#      main="",
#      col="blue", lwd=2)
# abline(h=0)
# hist(rFMNN, main="", xlab="returns", col="slateblue1")
# par(mfrow=c(1,1))
# 
# par(mfrow=c(2,2))
# hist(rFMNN, xlab="return",ylab="frequency", 
#      main="cc returns from FM with Patient and Low-Int Agent", 
#      col="slateblue1")
# boxplot(rFMNN, col="slateblue1")
# plot(density(rFMNN,type="l",xlab="return",ylab="density",
#              lwd=2, col="slateblue1", main="smoothed densiy"))
# qqnorm(rFMNN, col="blue")
# qqline(rFMNN)
# par(mfrow=c(1,1))

# rFMNNggHist <- ggplot(data=rFMNNDataFrame, aes(rFMNNDataFrame$rFMNN)) + 
#         geom_histogram(aes(y =..density..), 
#                        #breaks=seq(20, 50, by = 2), 
#                        col="red", 
#                        fill="blue", 
#                        #alpha = .2
#         ) + 
#         geom_density(col=2) + 
#         labs(title="Rozk³ad stóp zwrotu modelu z agentami 
# cierpliwymi i low-intelligence") +
#         labs(x="Wartoœæ stopy zwrotu", y="Czêstotliwoœæ wystêpowania", size=8)+
#         theme(plot.title=element_text(size=8, face="bold"),
#               axis.title=element_text(size=8))
# 
# rFMNNggBoxPlot <- ggplot(data=rFMNNDataFrame, aes(x = rFMNNDataFrame$step, 
#                                               y = rFMNNDataFrame$rFMNN)) + 
#         geom_boxplot( 
#                 #breaks=seq(20, 50, by = 2), 
#                 col="red", 
#                 fill="blue", 
#                 #alpha = .2
#         ) +  
#         labs(title="Wartoœci stóp zwrotu modelu z agentami 
# cierpliwymi i low-intelligence") +
#         labs( x = "krok czasowy", y="stopa zwrotu", size=8)+
#         theme(plot.title=element_text(size=8, face="bold", hjust=0.5),
#               axis.title=element_text(size=8))

# rFMNNggPlot
# rFMNNggHist
# rFMNNggBoxPlot
```

# ```{r multiplotOfReturnsFMNN, echo=FALSE}
# pdf('figure/multiplotOfReturnsFMNN.pdf')
# multiplot(rFMNNggPlot, rFMNNggHist, rFMNNggBoxPlot, rows=3)
# dev.off()
# ```

```{r multiplotOfReturnsFMPlusNNCOde, include=FALSE}
# rFMPlusNN
# muhatRFMPlusNN
# sigmahatRFMPlusNN
# 
# par(mfrow=c(1,2))
# plot(rFMPlusNN,ylab="returns",
#      main="",
#      col="blue", lwd=2)
# abline(h=0)
# hist(rFMPlusNN, main="", xlab="returns", col="slateblue1")
# par(mfrow=c(1,1))
# 
# par(mfrow=c(2,2))
# hist(rFMPlusNN, xlab="return",ylab="frequency", 
#      main="cc returns from FM with Patient, 
#      Impatient and Low-Int Agent", 
#      col="slateblue1")
# boxplot(rFMPlusNN, col="slateblue1")
# plot(density(rFMPlusNN,type="l",xlab="return",ylab="density",
#              lwd=2, col="slateblue1", main="smoothed densiy"))
# qqnorm(rFMPlusNN, col="blue")
# qqline(rFMPlusNN)
# par(mfrow=c(1,1))

# rFMPlusNNggHist <- ggplot(data=rFMPlusNNDataFrame, 
#                           aes(rFMPlusNNDataFrame$rFMPlusNN)) + 
#         geom_histogram(aes(y =..density..), 
#                        #breaks=seq(20, 50, by = 2), 
#                        col="red", 
#                        fill="blue", 
#                        #alpha = .2
#         ) + 
#         geom_density(col=2) + 
#         labs(title="Rozk³ad stóp zwrotu modelu z agentami
#              cierpliwymi, niecierpliwymi i low-intelligence") +
#         labs(x="Wartoœæ stopy zwrotu", y="Czêstotliwoœæ wystêpowania", size=8)+
#         theme(plot.title=element_text(size=8, face="bold"),
#               axis.title=element_text(size=8))
# 
# rFMPlusNNggBoxPlot <- ggplot(data=rFMPlusNNDataFrame, 
#                              aes(x = rFMPlusNNDataFrame$step, 
#                                         y = rFMPlusNNDataFrame$rFMPlusNN)) + 
#         geom_boxplot( 
#                 #breaks=seq(20, 50, by = 2), 
#                 col="red", 
#                 fill="blue", 
#                 #alpha = .2
#         ) +  
#         labs(title="Wartoœci stóp zwrotu modelu z agentami
# cierpliwymi, niecierpliwymi i low-intelligence") +
#         labs( x = "krok czasowy", y="stopa zwrotu", size=8)+
#         theme(plot.title=element_text(size=8, face="bold", hjust=0.5),
#               axis.title=element_text(size=8))

# rFMPlusNNggPlot
# rFMPlusNNggHist
# rFMPlusNNggBoxPlot
```

# ```{r multiplotOfReturnsFMPlusNN, echo=FALSE}
# pdf('figure/multiplotOfReturnsFMPlusNN.pdf')
# multiplot(rFMPlusNNggPlot, rFMPlusNNggHist, rFMPlusNNggBoxPlot, rows=3)
# dev.off()
# ```
# 
# ```{r multiplotOfReturnsALLPlot, echo=FALSE }
# pdf('figure/multiplotOfReturnsALL.pdf')
# #all returnggPlot on one 
# multiplot(rFMggPlot, rFMNNggPlot, rFMPlusNNggPlot, rows=3)
# dev.off()
# ```

# ```{r multiplotOfReturnsALLPlotHistBox, echo=FALSE}
# pdf('figure/multiplotOfReturnsALLPlotHistBox.pdf')
# multiplot(rFMggPlot, rFMNNggPlot, rFMPlusNNggPlot,
#           rFMggHist, rFMNNggHist, rFMPlusNNggHist,  
#           rFMggBoxPlot, rFMNNggBoxPlot, rFMPlusNNggBoxPlot,
#           cols=3)
# dev.off()
# ```

```{r  multiplotOfVolumeFMCode, include=FALSE}
#vFM
# vFMggHist <- ggplot(data=vFMDataFrame, aes(vFMDataFrame$vFM)) + 
#         geom_histogram(aes(y =..density..), 
#                        #breaks=seq(20, 50, by = 2), 
#                        col="red", 
#                        fill="blue", 
#                        #alpha = .2
#         ) + 
#         geom_density(col=2) + 
#         labs(title="Rozk³ad wolumenu modelu z agentami
#              cierpliwymi i niecierpliwymi") +
#         labs(x="Wartoœæ wolumenu", y="Czêstotliwoœæ wystêpowania")+
#         theme(plot.title=element_text(size=14, face="bold"),
#               axis.title=element_text(size=8))
# 
# vFMggBoxPlot <- ggplot(data=vFMDataFrame, aes(x = vFMDataFrame$step, 
#                                               y = vFMDataFrame$vFM)) + 
#         geom_boxplot( 
#                 #breaks=seq(20, 50, by = 2), 
#                 col="red", 
#                 fill="blue", 
#                 #alpha = .2
#         ) +  
#         labs(title="Wartoœæ wolumenu modelu z agentami
#              cierpliwymi i niecierpliwymi") +
#         labs( x = "krok czasowy", y="wolumen")+
#         theme(plot.title=element_text(size=14, face="bold"),
#               axis.title=element_text(size=8))
# 
# # vFMggPlot
# # vFMggHist
# # vFMggBoxPlot
# ```
# 
# ```{r }
# pdf('figure/multiplotOfVolumeFM.pdf')
# multiplot(vFMggPlot, vFMggHist, vFMggBoxPlot, rows=3)
# dev.off()
# ```
# 
# ```{r multiplotOfVolumeFMNNCode, include=FALSE}
# #vFMNN
# vFMNNggHist <- ggplot(data=vFMNNDataFrame, aes(vFMNNDataFrame$vFMNN)) + 
#         geom_histogram(aes(y =..density..), 
#                        #breaks=seq(20, 50, by = 2), 
#                        col="red", 
#                        fill="blue", 
#                        #alpha = .2
#         ) + 
#         geom_density(col=2) + 
#         labs(title="Rozk³ad wolumenu modelu z agentami
#              cierpliwymi i low-intelligence") +
#         labs(x="Wartoœæ wolumenu", y="Czêstotliwoœæ wystêpowania")+
#         theme(plot.title=element_text(size=14, face="bold"),
#               axis.title=element_text(size=8))
# 
# vFMNNggBoxPlot <- ggplot(data=vFMNNDataFrame, aes(x = vFMNNDataFrame$step, 
#                                                   y = vFMNNDataFrame$vFMNN)) + 
#         geom_boxplot( 
#                 #breaks=seq(20, 50, by = 2), 
#                 col="red", 
#                 fill="blue", 
#                 #alpha = .2
#         ) +  
#         labs(title="Wartoœci wolumenu modelu z agentami
#              cierpliwymi i low-intelligence") +
#         labs( x = "krok czasowy", y="wolumen")+
#         theme(plot.title=element_text(size=14, face="bold"),
#               axis.title=element_text(size=8))
# 
# # vFMNNggPlot
# # vFMNNggHist
# # vFMNNggBoxPlot
# ```
# 
# ```{r multiplotOfVolumeFMPlusNN, echo=FALSE}
# pdf('figure/multiplotOfVolumeFMNN.pdf')
# multiplot(vFMNNggPlot, vFMNNggHist, vFMNNggBoxPlot, rows=3)
# dev.off()
# ```
# 
# ```{r multiplotOfVolumeFMPlusNNCode, include=FALSE}
# #vFMPlusNN
# vFMPlusNNggHist <- ggplot(data=vFMPlusNNDataFrame, 
#                           aes(vFMPlusNNDataFrame$vFMPlusNN)) + 
#         geom_histogram(aes(y =..density..), 
#                        #breaks=seq(20, 50, by = 2), 
#                        col="red", 
#                        fill="blue", 
#                        #alpha = .2
#         ) + 
#         geom_density(col=2) + 
#         labs(title="Rozk³ad wolumenu modelu z agentami
#              cierpliwymi, niecierpliwymi i low-intelligence") +
#         labs(x="Wartoœæ wolumenu", y="Czêstotliwoœæ wystêpowania")+
#         theme(plot.title=element_text(size=14, face="bold"),
#               axis.title=element_text(size=8))
# 
# vFMPlusNNggBoxPlot <- ggplot(data=vFMPlusNNDataFrame, 
#                              aes(x = vFMPlusNNDataFrame$step, 
#                                  y = vFMPlusNNDataFrame$vFMPlusNN)) + 
#         geom_boxplot( 
#                 #breaks=seq(20, 50, by = 2), 
#                 col="red", 
#                 fill="blue", 
#                 #alpha = .2
#         ) +  
#         labs(title="Wartoœci wolumenu modelu z agentami
#              cierpliwymi, niecierpliwymi i low-intelligence") +
#         labs( x = "krok czasowy", y="wolumen")+
#         theme(plot.title=element_text(size=14, face="bold"),
#               axis.title=element_text(size=8))
# # 
# # vFMPlusNNggPlot
# # vFMPlusNNggHist
# # vFMPlusNNggBoxPlot
```
# 
# ```{r multiplotOfVolumeFMPlusNN, echo=FALSE}
# pdf('figure/multiplotOfVolumeFMPlusNN.pdf')
# multiplot(vFMPlusNNggPlot, vFMPlusNNggHist, vFMPlusNNggBoxPlot, rows=3)
# dev.off()
# ```
# 
# ```{r multiplotOfVolumeFMPlusNN, echo=FALSE}
# pdf('figure/multiplotOfVolumeALLPlot.pdf')
# #all volumeggPlot on one 
# multiplot(vFMggPlot, vFMNNggPlot, vFMPlusNNggPlot, rows=3)
# dev.off()
# ```
# 
# ```{r multiplotOfVolumeALLPlotHistBox, echo=FALSE}
# pdf('figure/multiplotOfVolumeALLPlotHistBox.pdf')
# multiplot(vFMggPlot, vFMNNggPlot, vFMPlusNNggPlot,
#           vFMggHist, vFMNNggHist, vFMPlusNNggHist,  
#           vFMggBoxPlot, vFMNNggBoxPlot, vFMPlusNNggBoxPlot,
#           cols=3)
# dev.off()
# ```
# 
# ```{r errorPlot, include=FALSE}
# # eNN
# muhatENN
# sigmahatENN
# 
# par(mfrow=c(1,3))
# plot(eNN,ylab="errorNN",
#      main="B³¹d predykcji wartoœci kolejnej stopy zwrotu
#      modelu FMNN",
#      col="blue", lwd=2)
# abline(h=0)
# hist(eNN, main="Rozk³ad wartoœci b³êdu predykcji wartoœci kolejnej stopy zwrotu
#      modelu FMNN", xlab="errors", col="slateblue1")
# boxplot(eNN, col="slateblue1")
# par(mfrow=c(1,1))
# 
# par(mfrow=c(2,2))
# hist(eNN, xlab="errorNN",ylab="frequency", 
#      main="error in model with Low-Int Agent", 
#      col="slateblue1")
# boxplot(eNN, col="slateblue1")
# plot(density(eNN,type="l",xlab="errorNN",ylab="density",
#              lwd=2, col="slateblue1", main="smoothed densiy"))
# qqnorm(eNN, col="blue")
# qqline(eNN)
# par(mfrow=c(1,1))
# 
# # ePlus
# muhatEPlus
# sigmahatEPlus
# 
# par(mfrow=c(1,3))
# plot(ePlus,ylab="errorPlus",
#      main="B³¹d predykcji wartoœci kolejnej stopy zwrotu
#      modelu FMPlusNN",
#      col="blue", lwd=2)
# abline(h=0)
# hist(ePlus, main="", xlab="errorsPlus", col="slateblue1")
# boxplot(ePlus, col="slateblue1")
# par(mfrow=c(1,1))
# 
# par(mfrow=c(2,2))
# hist(ePlus, xlab="errorPlus",ylab="frequency", 
#      main="error in Farmers model with Patient, Impatient and Low-Int Agent", 
#      col="slateblue1")
# boxplot(ePlus, col="slateblue1")
# plot(density(ePlus,type="l",xlab="errorPlus",ylab="density",
#              lwd=2, col="slateblue1", main="smoothed densiy"))
# qqnorm(ePlus, col="blue")
# qqline(ePlus)
# par(mfrow=c(1,1))
# ```

```{r SkewnessAndKurtosis, echo=FALSE}
# skewness(rFM)
# kurtosis(rFM)
# skewness(rFMNN)
# kurtosis(rFMNN)
# skewness(rFMPlusNN)
# kurtosis(rFMPlusNN)

# skewness(vFM)
# kurtosis(vFM)
# skewness(vFMNN)
# kurtosis(vFMNN)
# skewness(vFMPlusNN)
# kurtosis(vFMPlusNN)
```


```{r RwFMCode, include=FALSE}
# 4. Random Walk z parametrami symulacji (sym. Mason).
#rFM
## substitute data from (mu, sigma etc) from FM to RW model and comparete them
# RW modle Y_t = Y_0 + sum(e) where e~~ GWN(0, sd(..)^2) or ~iid N(0, sd(..)^2)
# E[Y_t] = Y_0; var(Y_t) = sum(var_e) = t * sd(e)^2
# rFM1
y0RwRFM = muRwRFM = muhatRFM
sdErrorRFM = sd(rFM)
nobs = length(rFM)
set.seed(111)
# e-Error ~ GWN(0, sd(..)^2) or ~iid N(0, sd(..)^2)  (GWN-GaussianWhiteNoiseDistribution)
# independent => cov(Y_t, Y_t1)=0
simErrorRFM = rnorm(nobs, mean=0, sd=sdErrorRFM)
# (general) lnP_t = lnP_0 + sum(return) 
# (for CER model r = mu + e) => lnP_t = lnP_0 + t*mu + sum(error_t)
simRwRFMLogPrice = y0RwRFM + cumsum(simErrorRFM)
# to calculate prices we make exp(lnP)
simRwRFMPrice = exp(simRwRFMLogPrice)
```

```{r simRwRFMPlot, echo=FALSE}
pdf("figure/simRwRFMPlot.pdf")
par(mfrow=c(2,1))
# par(mar = c(2,2,2,2))
ts.plot(simRwRFMLogPrice, col="blue",lwd=2,
#         ylim=c(-15, 2),
        ylab="logarytmiczna wartoœæ zmiany ceny", xlab = "krok czasowy",
        main="Wyniki symulacji zmiany logarytmu ceny w czasie 
wed³ug modelu b³¹dzenia losowego sprametryzowanego 
wed³ug wyników symulacji modelu FM")
lines( rep.int(y0RwRFM, nobs), col="red", lty="dotted", lwd=3)
lines(cumsum(simErrorRFM), col="orange", lty="dashed", lwd=0.5)
abline(h=0, lwd=0.5)
legend(x="topleft",legend=c("ln(p(t))","E[ln(p(t))]","ln(p(t))-E[ln(p(t))]"),
       lty=c("solid","dotted","dashed"), col=c("blue","red","orange"), 
       lwd=2, cex=c(0.75,0.75,0.75))
ts.plot(simRwRFMPrice, lwd=2, col="blue", ylab="cena p(t)", 
        xlab = "krok czasowy",
        main="Wyniki symulacji zmiany ceny w czasie wed³ug modelu 
b³¹dzenia losowego sprametryzowanego wed³ug 
wyników symulacji modelu FM")
dev.off()
```

```{r RwRFMNN, include=FALSE}
# par(mfrow=c(1,1))
# dev.off()

#rFMNN
## substitute data from (mu, sigma etc) from FM to RW model and comparete them
# RW modle Y_t = Y_0 + sum(e) where e~~ GWN(0, sd(..)^2) or ~iid N(0, sd(..)^2)
# E[Y_t] = Y_0; var(Y_t) = sum(var_e) = t * sd(e)^2
# rFM1
y0RwRFMNN = muRwRFMNN = muhatRFMNN
sdErrorRFMNN = sd(rFMNN)
nobs = length(rFMNN)
set.seed(111)
# e-Error ~ GWN(0, sd(..)^2) or ~iid N(0, sd(..)^2)  (GWN-GaussianWhiteNoiseDistribution)
# independent => cov(Y_t, Y_t1)=0
simErrorRFMNN = rnorm(nobs, mean=0, sd=sdErrorRFMNN)
# (general) lnP_t = lnP_0 + sum(return) 
# (for CER model r = mu + e) => lnP_t = lnP_0 + t*mu + sum(error_t)
simRwRFMNNLogPrice = y0RwRFMNN + cumsum(simErrorRFMNN)
# to calculate prices we make exp(lnP)
simRwRFMNNPrice = exp(simRwRFMNNLogPrice)
```

```{r RwRFMNNPlot, echo=FALSE}
pdf("figure/simRwRFMNNPlot.pdf")
par(mfrow=c(2,1))
# par(mar = c(2,2,2,2))
ts.plot(simRwRFMNNLogPrice, col="blue",lwd=2,
#         ylim=c(-5, 2),
        ylab="logarytmiczna wartoœæ zmiany ceny", xlab = "krok czasowy", 
        main="Wyniki symulacji zmiany logarytmu ceny w czasie 
wed³ug modelu b³¹dzenia losowego sprametryzowanego 
wed³ug wyników symulacji modelu FMNN")
lines( rep.int(y0RwRFMNN, nobs), col="red", lty="dotted", lwd=3)
lines(cumsum(simErrorRFMNN), col="orange", lty="dashed", lwd=0.5)
abline(h=0, lwd=0.5)
legend(x="topleft",legend=c("ln(p(t))","E[ln(p(t))]","ln(p(t))-E[ln(p(t))]"),
       lty=c("solid","dotted","dashed"), col=c("blue","red","orange"), 
       lwd=2, cex=c(0.75,0.75,0.75))
ts.plot(simRwRFMNNPrice, lwd=2, col="blue", 
        ylab="cena p(t)", xlab = "krok czasowy", 
        main="Wyniki symulacji zmiany ceny w czasie wed³ug modelu 
b³¹dzenia losowego sprametryzowanego wed³ug 
wyników symulacji modelu FMNN")

# par(mfrow=c(1,1))
dev.off()
```

```{r RwRFMPlusNN, include=FALSE}
#rFMPlusNN
## substitute data from (mu, sigma etc) from FM to RW model and comparete them
# RW modle Y_t = Y_0 + sum(e) where e~~ GWN(0, sd(..)^2) or ~iid N(0, sd(..)^2)
# E[Y_t] = Y_0; var(Y_t) = sum(var_e) = t * sd(e)^2
# rFM1
y0RwRFMPlusNN = muRwRFMPlusNN = muhatRFMPlusNN
sdErrorRFMPlusNN = sd(rFMPlusNN)
nobs = length(rFMPlusNN)
set.seed(111)
# e-Error ~ GWN(0, sd(..)^2) or ~iid N(0, sd(..)^2)  (GWN-GaussianWhiteNoiseDistribution)
# independent => cov(Y_t, Y_t1)=0
simErrorRFMPlusNN = rnorm(nobs, mean=0, sd=sdErrorRFMPlusNN)
# (general) lnP_t = lnP_0 + sum(return) 
# (for CER model r = mu + e) => lnP_t = lnP_0 + t*mu + sum(error_t)
simRwRFMPlusNNLogPrice = y0RwRFMPlusNN + cumsum(simErrorRFMPlusNN)
# to calculate prices we make exp(lnP)
simRwRFMPlusNNPrice = exp(simRwRFMPlusNNLogPrice)
```

```{r simRwRFMPlusNNPlot, echo=FALSE}
pdf("figure/simRwRFMPlusNNPlot.pdf")
par(mfrow=c(2,1))
# par(mar = c(2,2,2,2))
ts.plot(simRwRFMPlusNNLogPrice, col="blue",lwd=2,
#         ylim=c(-10, 2),
        ylab="logarytmiczna wartoœæ zmiany ceny", xlab = "krok czasowy",
        main="Wyniki symulacji zmiany logarytmu ceny w czasie 
wed³ug modelu b³¹dzenia losowego sprametryzowanego 
wed³ug wyników symulacji modelu FMPlusNN")
lines( rep.int(y0RwRFMPlusNN, nobs), col="red", lty="dotted", lwd=3)
lines(cumsum(simErrorRFMPlusNN), col="orange", lty="dashed", lwd=0.5)
abline(h=0, lwd=0.5)
legend(x="topleft",legend=c("ln(p(t))","E[ln(p(t))]","ln(p(t))-E[ln(p(t))]"),
       lty=c("solid","dotted","dashed"), col=c("blue","red","orange"), 
       lwd=2, cex=c(0.75,0.75,0.75))
ts.plot(simRwRFMPlusNNPrice, lwd=2, col="blue", 
        ylab="cena p(t)", xlab = "krok czasowy",
        main="Wyniki symulacji zmiany ceny w czasie 
wed³ug modelu b³¹dzenia losowego sprametryzowanego 
wed³ug wyników symulacji modelu FMPlusNN")

# par(mfrow=c(1,1))
dev.off()
```

```{r compareSimRwRFMAll, echo=FALSE}
# Analiza z danymi RW
# compare simRwRFMLogPrice, simRwRFMNNLogPrice and simRwRFMPlusNNLogPrice
pdf("figure/simRwRFMAllCompare.pdf")
par(mfrow=c(3,1))
# par(mar = c(2,2,2,2))
ts.plot(simRwRFMLogPrice, col="blue",lwd=2,
#         ylim=c(-15, 2),
        ylab="logarytmiczna wartoœæ zmiany ceny", xlab="krok czasowy",
        main="Wyniki symulacji zmiany logarytmu ceny w czasie 
wed³ug modelu b³¹dzenia losowego sprametryzowanego 
wed³ug wyników symulacji modelu FM")
lines( rep.int(y0RwRFM, nobs), col="red", lty="dotted", lwd=3)
ts.plot(simRwRFMNNLogPrice, col="blue",lwd=2,
#         ylim=c(-10, 2),
        ylab="logarytmiczna wartoœæ zmiany ceny", xlab="krok czasowy", 
        main="Wyniki symulacji zmiany logarytmu ceny w czasie 
wed³ug modelu b³¹dzenia losowego sprametryzowanego 
wed³ug wyników symulacji modelu FMNN")
lines( rep.int(y0RwRFMNN, nobs), col="red", lty="dotted", lwd=3)
ts.plot(simRwRFMPlusNNLogPrice, col="blue",lwd=2,
#         ylim=c(-11, 2),
        ylab="logarytmiczna wartoœæ zmiany ceny", xlab="krok czasowy",
        main="Wyniki symulacji zmiany logarytmu ceny w czasie 
wed³ug modelu b³¹dzenia losowego sprametryzowanego 
wed³ug wyników symulacji modelu FMPlusNN")
lines(rep.int(y0RwRFMPlusNN, nobs), col="red", lty="dotted", lwd=3)
dev.off()
```

```{r RwRFMBoxPlot, include=FALSE}
# reteurns based on RwRFM1
returnRwRFM <- simErrorRFM
returnRwRFMNN <- simErrorRFMNN
returnRwRFMPlusNN <- simErrorRFMPlusNN

#ggPlot of returnRwRFM
returnRwRFMDataFrame <- as.data.frame(returnRwRFM)
returnRwRFMDataFrame$step <- c(1:length(returnRwRFM))
returnRwRFMggPlot <- ggplot(data = returnRwRFMDataFrame, 
                    aes(x = step , y = returnRwRFM))
returnRwRFMggPlot <- returnRwRFMggPlot + geom_line(colour = "red")
returnRwRFMggPlot <- returnRwRFMggPlot + 
        geom_abline(colour = "blue", slope = 0)
returnRwRFMggPlot <- returnRwRFMggPlot + 
                        ggtitle("Stopy zwrotu z symulacji modelu 
b³¹dzenia losowego sparametryzowanego 
wed³ug modelu FM") + 
        xlab("krok czasowy") + 
        ylab("stopa zwrotu") +
        theme(plot.title=element_text(size=8, face="bold", hjust=0.5),
              axis.title=element_text(size=8))
#         scale_y_continuous(breaks=seq(-.9, 1.2, 0.1))

# returnRwRFMggPlot
# ggsave("returnRwRFMggPlot.png", plot = returnRwRFMggPlot, 
#        width = 4, height = 4)

#ggBOXPlot of returnRwRFM
returnRwRFMggBoxPlot <- ggplot(data=returnRwRFMDataFrame, 
                               aes(x = returnRwRFMDataFrame$step, 
                                y = returnRwRFMDataFrame$returnRwRFM)) + 
        geom_boxplot( 
                #breaks=seq(20, 50, by = 2), 
                col="red", 
                fill="blue", 
                #alpha = .2
        ) +  
        labs(title="Wartoœci stóp zwrotu 
modelu b³¹dzenia losowego 
sparametryzowanego wed³ug modelu FM") +
        labs( x = "krok czasowy", y="stopa zwrotu")+
        theme(plot.title=element_text(size=8, face="bold", hjust=0),
              axis.title=element_text(size=8))

#ggPlot of returnRwRFMNN
returnRwRFMNNDataFrame <- as.data.frame(returnRwRFMNN)
returnRwRFMNNDataFrame$step <- c(1:length(returnRwRFMNN))
returnRwRFMNNggPlot <- ggplot(data = returnRwRFMNNDataFrame, 
                            aes(x = step , y = returnRwRFMNN))
returnRwRFMNNggPlot <- returnRwRFMNNggPlot + geom_line(colour = "red")
returnRwRFMNNggPlot <- returnRwRFMNNggPlot + 
        geom_abline(colour = "blue", slope = 0)
returnRwRFMNNggPlot <- returnRwRFMNNggPlot + 
        ggtitle("Stopy zwrotu z symulacji modelu 
b³¹dzenia losowego sparametryzowanego
wed³ug modelu FMNN") + 
        xlab("krok czasowy") + 
        ylab("stopa zwrotu") +
        theme(plot.title=element_text(size=8, face="bold", hjust=0.5),
              axis.title=element_text(size=8))
#         scale_y_continuous(breaks=seq(-.9, 1.2, 0.1))

# returnRwRFMNNggPlot
# ggsave("returnRwRFMNNggPlot.png", plot = returnRwRFMNNggPlot, 
#        width = 4, height = 4)

#ggBOXPlot of returnRwRFMNN
returnRwRFMNNggBoxPlot <- ggplot(data=returnRwRFMNNDataFrame, 
                               aes(x = returnRwRFMNNDataFrame$step, 
                                   y = returnRwRFMNNDataFrame$returnRwRFMNN)) + 
        geom_boxplot( 
                #breaks=seq(20, 50, by = 2), 
                col="red", 
                fill="blue", 
                #alpha = .2
        ) +  
        labs(title="Wartoœci stóp zwrotu modelu 
b³¹dzenia losowego sparametryzowanego
wed³ug modelu FMNN") +
        labs( x = "krok czasowy", y="stopa zwrotu") +
        theme(plot.title=element_text(size=8, face="bold", hjust=0.5),
              axis.title=element_text(size=8))


#ggPlot of returnRwRFMPlusNN
returnRwRFMPlusNNDataFrame <- as.data.frame(returnRwRFMPlusNN)
returnRwRFMPlusNNDataFrame$step <- c(1:length(returnRwRFMPlusNN))
returnRwRFMPlusNNggPlot <- ggplot(data = returnRwRFMPlusNNDataFrame, 
                              aes(x = step , y = returnRwRFMPlusNN))
returnRwRFMPlusNNggPlot <- returnRwRFMPlusNNggPlot + geom_line(colour = "red")
returnRwRFMPlusNNggPlot <- returnRwRFMPlusNNggPlot + 
        geom_abline(colour = "blue", slope = 0)
returnRwRFMPlusNNggPlot <- returnRwRFMPlusNNggPlot + 
        ggtitle("Stopy zwrotu z symulacji modelu 
b³¹dzenia losowego sparametryzowanego
wed³ug modelu FMPlusNN") + 
        xlab("krok czasowy") + 
        ylab("stopa zwrotu") +
        theme(plot.title=element_text(size=8, face="bold", hjust=0.5),
              axis.title=element_text(size=8))
#         scale_y_continuous(breaks=seq(-.9, 1.2, 0.1))

# returnRwRFMPlusNNggPlot
# ggsave("returnRwRFMPlusNNggPlot.png", plot = returnRwRFMPlusNNggPlot, 
#        width = 4, height = 4)

#ggBOXPlot of returnRwRFMPlusNN
returnRwRFMPlusNNggBoxPlot <- ggplot(data=returnRwRFMPlusNNDataFrame, 
                                 aes(x = returnRwRFMPlusNNDataFrame$step, 
                           y = returnRwRFMPlusNNDataFrame$returnRwRFMPlusNN)) + 
        geom_boxplot( 
                #breaks=seq(20, 50, by = 2), 
                col="red", 
                fill="blue", 
                #alpha = .2
        ) +  
        labs(title="Wartoœci stóp zwrotu modelu 
b³¹dzenia losowego sparametryzowanego
wed³ug modelu FMPlusNN") +
        labs( x = "krok czasowy", y="stopa zwrotu")+
        theme(plot.title=element_text(size=8, face="bold", hjust=0.5),
              axis.title=element_text(size=8))
```

```{r RwCompareToRFM, echo=FALSE}
pdf("figure/RwCompareToRFM.pdf")
multiplot(rFMggPlot, rFMggBoxPlot, 
          returnRwRFMggPlot, returnRwRFMggBoxPlot,
          cols=2)
dev.off()
```

```{r RwCompareToRFMNN, echo=FALSE}
pdf("figure/RwCompareToRFMNN.pdf")
multiplot(rFMNNggPlot, rFMNNggBoxPlot,
          returnRwRFMNNggPlot, returnRwRFMNNggBoxPlot,
          cols=2)
dev.off()
```

```{r RwCompareToRFMPlusNN, echo=FALSE}
pdf("figure/RwCompareToRFMPlusNN.pdf")
multiplot(rFMPlusNNggPlot, rFMPlusNNggBoxPlot,
          returnRwRFMPlusNNggPlot, returnRwRFMPlusNNggBoxPlot,
          cols=2)
dev.off()
```


```{r RwRFMAllboxPlotCompare, include=FALSE}
# # compare rFM, returnRwRFM, returnRwRFMNN, returnRwRFMPlusNN
# #         and SP500 data to simulated gaussian data
# par(mfrow=c(5,1))
# par(mar = c(2,2,2,2))
# boxplot(rFM[ (length(rFM)-length(returns.mat[,"sp500"])+1) : length(rFM) ], 
#         returnRwRFM, SP500, 
#         col="slateblue1", names=c("return from FM Farmer", "returnRwRFM", 
#                                   "return from SP500"))
# boxplot(rFMNN[ (length(rFMNN)-length(returns.mat[,"sp500"])+1) : length(rFMNN) ]
#         , returnRwRFMNN,  SP500,
#         col="slateblue1", names=c("return from FM with Low-Int Agent", 
#                                   "returnRwRFMNN", "return from SP500"))
# boxplot(rFMPlusNN[ (length(rFMPlusNN)-length(returns.mat[,"sp500"])+1) : 
#                            length(rFMPlusNN) ], returnRwRFMPlusNN,  SP500,
#         col="slateblue1", names=c("return from FM Farmer with Low-Int Agent", 
#                                   "returnRwRFMPlusNN", "return from SP500"))
pdf("figure/RwRFMAllboxPlotCompare.pdf")
par(mfrow=c(2,1))
boxplot(rFM, rFMNN, rFMPlusNN,  cex=0.5,
        col="slateblue1", names=c("stopy zwrotu FM",
                                  "stopy zwrotu FMNN",
                                  "stopy zwrotu FMPlusNN"),
        main = "Wartoœci stóp zwrotu 
                trzech modeli: FM, FMNN, FMPlusNN", cex.main=1,
        ylabel = "stopa zwrtou", cex=0.25)
boxplot(returnRwRFM, returnRwRFMNN, returnRwRFMPlusNN,
        col="slateblue1", names=c("stopy zwrotu RwRFM", "stopy zwrotu RwRFMNN", 
                                  "stopy zwrotu RwRFMPlusNN"),
        main = "Wartoœci stóp zwrotu otrzymanych
        z symulacji modelu b³¹dzenia losowegostóp sparametryzowanego w oparciu 
        o wyniki z modeli: FM, FMNN, FMPlusNN", cex.main=1,
        ylabel = "stopa zwrtou", cex=0.25)
# par(mfrow=c(1,1))
dev.off()
```
```{r SkewKurtosisCode, include=FALSE}
# # rFM        
# # compute quantiles and compare to normal quantiles
# pvals = c(0.01,0.05,0.25,0.5,0.75,0.95,0.99)
# quantile(probs=pvals,rFM)
# qnorm(p=pvals,mean=muhatRFM,sd=sd(rFM))
# # returnRwRFM
# pvals = c(0.01,0.05,0.25,0.5,0.75,0.95,0.99)
# quantile(probs=pvals,returnRwRFM)
# qnorm(p=pvals,mean=muRwRFM,sd=sd(returnRwRFM))       

# rbind(qnorm(p=pvals,mean=muhatRFM,sd=sd(rFM)), 
#       qnorm(p=pvals,mean=muRwRFM,sd=sd(returnRwRFM))  )
# rbind(quantile(probs=pvals,rFM), quantile(probs=pvals,returnRwRFM) )     
# compute descriptive statistics
mean(rFM)
sd(rFM)
skewness(rFM)
kurtosis(rFM)
# returnRwRFM
mean(returnRwRFM)
sd(returnRwRFM)
skewness(returnRwRFM)
kurtosis(returnRwRFM)

# skewness(rFM)
# kurtosis(rFM)
# skewness(rFMNN)
# kurtosis(rFMNN)
# skewness(rFMPlusNN)
# kurtosis(rFMPlusNN)

# skewness(vFM)
# kurtosis(vFM)
# skewness(vFMNN)
# kurtosis(vFMNN)
# skewness(vFMPlusNN)
# kurtosis(vFMPlusNN)

# # rFMNN        
# # compute quantiles and compare to normal quantiles
# pvals = c(0.01,0.05,0.25,0.5,0.75,0.95,0.99)
# quantile(probs=pvals,rFMNN)
# qnorm(p=pvals,mean=muhatRFMNN,sd=sd(rFMNN))
# # returnRwRFMNN
# pvals = c(0.01,0.05,0.25,0.5,0.75,0.95,0.99)
# quantile(probs=pvals,returnRwRFMNN)
# qnorm(p=pvals,mean=muRwRFMNN,sd=sd(returnRwRFMNN))       
# 
# rbind(qnorm(p=pvals,mean=muhatRFMNN,sd=sd(rFMNN)), 
#       qnorm(p=pvals,mean=muRwRFMNN,sd=sd(returnRwRFMNN))  )
# rbind(quantile(probs=pvals,rFMNN), quantile(probs=pvals,returnRwRFMNN) )     
# compute descriptive statistics
mean(rFMNN)
sd(rFMNN)
skewness(rFMNN)
kurtosis(rFMNN)
# returnRwRFMNN
mean(returnRwRFMNN)
sd(returnRwRFMNN)
skewness(returnRwRFMNN)
kurtosis(returnRwRFMNN)

# # rFMPlusNN        
# # compute quantiles and compare to normal quantiles
# pvals = c(0.01,0.05,0.25,0.5,0.75,0.95,0.99)
# quantile(probs=pvals,rFMPlusNN)
# qnorm(p=pvals,mean=muhatRFMPlusNN,sd=sd(rFMPlusNN))
# # returnRwRFMPlusNN
# pvals = c(0.01,0.05,0.25,0.5,0.75,0.95,0.99)
# quantile(probs=pvals,returnRwRFMPlusNN)
# qnorm(p=pvals,mean=muRwRFMPlusNN,sd=sd(returnRwRFMPlusNN))       

# rbind(qnorm(p=pvals,mean=muhatRFMPlusNN,sd=sd(rFMPlusNN)), 
#       qnorm(p=pvals,mean=muRwRFMPlusNN,sd=sd(returnRwRFMPlusNN))  )
# rbind(quantile(probs=pvals,rFMPlusNN), quantile(probs=pvals,returnRwRFMPlusNN) )     
# compute descriptive statistics
mean(rFMPlusNN)
sd(rFMPlusNN)
skewness(rFMPlusNN)
kurtosis(rFMPlusNN)
# returnRwRFMPlusNN
mean(returnRwRFMPlusNN)
sd(returnRwRFMPlusNN)
skewness(returnRwRFMPlusNN)
kurtosis(returnRwRFMPlusNN)

sumDescriptiveStat <- 
        rbind(
        c(mean(rFM), sd(rFM), 
                skewness(rFM), kurtosis(rFM)),
#         c(mean(returnRwRFM), sd(returnRwRFM), 
#                 skewness(returnRwRFM), kurtosis(returnRwRFM)),
        c(mean(rFMNN),sd(rFMNN), 
                skewness(rFMNN), kurtosis(rFMNN)),
#         c(mean(returnRwRFMNN), sd(returnRwRFMNN),
#                 skewness(returnRwRFMNN), kurtosis(returnRwRFMNN)),
        c(mean(rFMPlusNN), sd(rFMPlusNN), 
                skewness(rFMPlusNN), kurtosis(rFMPlusNN))
#         c(mean(returnRwRFMPlusNN), sd(returnRwRFMPlusNN),
#                 skewness(returnRwRFMPlusNN), kurtosis(returnRwRFMPlusNN))
        )
colnames(sumDescriptiveStat) <- c("œr.", "odch.st.", "skoœnoœæ", "kurtoza")
rownames(sumDescriptiveStat) <- c("rFM", "rFMNN", "rFMPlusNN")
# rownames(sumDescriptiveStat) <- c("rFM", "RwRFM", "rFMNN", "RWRFMNN", 
#   "rFMPlusNN", "RWRFMPlusNN")
```

```{r SkewKurtosisRwAndFM, echo=FALSE}
round(sumDescriptiveStat, digits=4)
```


```{r ACF, echo=FALSE}
# 5. ACF - weryfikacja czy wystêpuje korelacja pomiêdzy kolejnymi wartoœciami - 
# Autocorrelation Function test
pdf("figure/ACF.pdf")
par(mfrow = c(3, 1))
# par(mar = c(3,3,3,3))
# rFM plot autocorrelations
tmpRFM = acf(rFM, main = "Autokorelacja wyników symulacji modelu FM")
# rFMNN plot autocorrelations
tmpRFMNN = acf(rFMNN, 
               main = "Autokorelacja wyników symulacji modelu FMNN")
# rFMPlusNN plot autocorrelations
tmpRFMPlusNN = acf(rFMPlusNN,
                main = "Autokorelacja wyników symulacji modelu FMPlusNN")
# par(mfrow = c(1, 1))
dev.off()
```

```{r acfRFMnotCorValuescode, include=FALSE}
# acf ci for rFM
corrRfm <- acf(rFM, type="correlation",plot=FALSE)
ciRfm <- qnorm((1 + 0.95)/2)/sqrt(sum(!is.na(rFM)))
ciRfm
corrValuesRfm <- corrRfm$acf
corrValuesRfm 
sum(corrValuesRfm > ciRfm | corrValuesRfm < -ciRfm) - 1 #subtract first element
```

```{r acfRFMnotCorValues, echo=FALSE}
acfRFMnotCorValues <- 
        corrValuesRfm [corrValuesRfm > ciRfm | corrValuesRfm < -ciRfm]
round(acfRFMnotCorValues[2:length(acfRFMnotCorValues)], digits=4)
```

```{r acfRwRFMnotCorValues, include=FALSE}
# acf ci for returnRwRFM
corrRfmRw <- acf(returnRwRFM, type="correlation",plot=FALSE)
ciRfmRw <- qnorm((1 + 0.95)/2)/sqrt(sum(!is.na(returnRwRFM)))
ciRfmRw
corrValuesRfmRw <- corrRfmRw$acf
corrValuesRfmRw 
sum(corrValuesRfmRw > ciRfmRw | corrValuesRfmRw < -ciRfmRw) - 1 #subtract first 
```

```{r acfRwRFMnotCorValues, echo=FALSE}
acfRwRFMnotCorValues <- 
        corrValuesRfmRw [corrValuesRfmRw > ciRfmRw | corrValuesRfmRw < -ciRfmRw]
round(acfRwRFMnotCorValues[2:length(acfRwRFMnotCorValues)], digits=4)
```

```{r acfRFMNNnotCorValuesCode, include=FALSE}
# acf ci for rFMNN
corrRfmNN <- acf(rFMNN, type="correlation",plot=FALSE)
ciRfmNN <- qnorm((1 + 0.95)/2)/sqrt(sum(!is.na(rFMNN)))
ciRfmNN
corrValuesRfmNN <- corrRfmNN$acf
corrValuesRfmNN 
sum(corrValuesRfmNN > ciRfmNN | corrValuesRfmNN < -ciRfmNN) - 1 #subtract first
                                                                        #element
```

```{r acfRFMNNnotCorValues, echo=FALSE}}
acfRFMNNnotCorValues <- 
        corrValuesRfmNN [corrValuesRfmNN > ciRfmNN | corrValuesRfmNN < -ciRfmNN]
round(acfRFMNNnotCorValues[2:length(acfRFMNNnotCorValues)], digits=4)
```

```{r acfRwRFMNNnotCorValuesCode, include=FALSE}
# acf ci for returnRwRFMNN
corrRfmNNRw <- acf(returnRwRFMNN, type="correlation",plot=FALSE)
ciRfmNNRw <- qnorm((1 + 0.95)/2)/sqrt(sum(!is.na(returnRwRFMNN)))
ciRfmNNRw
corrValuesRfmNNRw <- corrRfmNNRw$acf
corrValuesRfmNNRw 
sum(corrValuesRfmNNRw > ciRfmNNRw | corrValuesRfmNNRw < -ciRfmNNRw) - 1 
                                                        #subtract first element
```

```{r acfRwRFMNNnotCorValues, include=FALSE}
acfRwRFMNNnotCorValues <- 
        corrValuesRfmNNRw [corrValuesRfmNNRw > ciRfmNNRw | corrValuesRfmNNRw 
                   < -ciRfmNNRw]
round(acfRwRFMNNnotCorValues[2:length(acfRwRFMNNnotCorValues)], digits=4)
```

```{r acfRFMPlusNNnotCorValuesCode, include=FALSE}
# acf ci for rFMPlusNN
corrRfmPlusNN <- acf(rFMPlusNN, type="correlation",plot=FALSE)
ciRfmPlusNN <- qnorm((1 + 0.95)/2)/sqrt(sum(!is.na(rFMPlusNN)))
ciRfmPlusNN
corrValuesRfmPlusNN <- corrRfmPlusNN$acf
corrValuesRfmPlusNN 
sum(corrValuesRfmPlusNN > ciRfmPlusNN | corrValuesRfmPlusNN < -ciRfmPlusNN) - 1
                                                        #subtract first element
```

```{r acfRFMPlusNNnotCorValues, echo=FALSE}
acfRFMPlusNNnotCorValues <- 
        corrValuesRfmPlusNN [corrValuesRfmPlusNN > ciRfmPlusNN | 
                             corrValuesRfmPlusNN < -ciRfmPlusNN]
round(acfRFMPlusNNnotCorValues[2:length(acfRFMPlusNNnotCorValues)], digits=4)
```

```{r acfRwRFMPlusNNnotCorValuesCode, include=FALSE}
# acf ci for returnRwRFMPlusNN
corrRfmPlusNNRw <- acf(returnRwRFMPlusNN, type="correlation",plot=FALSE)
ciRfmPlusNNRw <- qnorm((1 + 0.95)/2)/sqrt(sum(!is.na(returnRwRFMPlusNN)))
ciRfmPlusNNRw
corrValuesRfmPlusNNRw <- corrRfmPlusNNRw$acf
corrValuesRfmPlusNNRw 
sum(corrValuesRfmPlusNNRw > ciRfmPlusNNRw | 
            corrValuesRfmPlusNNRw < -ciRfmPlusNNRw) - 1
# subtract first element
```

```{r acfRwRFMPlusNNnotCorValues, echo=FALSE}
acfRwRFMPlusNNnotCorValues <- 
        corrValuesRfmPlusNNRw [corrValuesRfmPlusNNRw > ciRfmPlusNNRw | 
                             corrValuesRfmPlusNNRw < -ciRfmPlusNNRw]
round(acfRwRFMPlusNNnotCorValues[2:length(acfRwRFMPlusNNnotCorValues)], 
      digits=4)
```

```{r acfrFMandRwFMPlot, echo=FALSE}
pdf("figure/acfrFMandRwFMPlot.pdf")
par(mfrow = c(2, 1))
# par(mar = c(3,3,3,3))
# rFM1 plot autocorrelations
tmpRFM <- acf(rFM, 
             main = "Autokorelacja wyników symulacji modelu FM")
# returnRwRFM plot autocorrelations
tmpRwRFM <- acf(returnRwRFM, 
           main = "Autokorelacja wyników symulacji modelu 
           b³adzenia losowego (wed³ug FM)")
# par(mfrow = c(1, 1))
dev.off()
```

```{r acfrFMNNandRwFMNNPlot, echo=FALSE}
pdf("figure/acfrFMNNandRwFMNNPlot.pdf")
par(mfrow = c(2, 1))
# par(mar = c(3,3,3,3))
# rFMNN plot autocorrelations
tmpRFMNN = acf(rFMNN,
               main = "Autokorelacja wyników symulacji modelu FMNN", 
               cex=0.5)
# returnRwRFMN plot autocorrelations
tmpRwRFMNN = acf(returnRwRFMNN,
                 main = "Autokorelacja wyników symulacji modelu 
           b³adzenia losowego (wed³ug FMNN)",
                 cex=0.5)
# par(mfrow = c(1, 1))
dev.off()
```

```{r acfrFMPlusNNandRwFMPluNNPlot, echo=FALSE}
pdf("figure/acfrFMPlusNNandRwFMPluNNPlot.pdf")
par(mfrow = c(2, 1))
# par(mar = c(3,3,3,3))
# rFMNN plot autocorrelations
tmpRFMPlusNN = acf(rFMPlusNN,
                main = "Autokorelacja wyników symulacji modelu FMPlusNN")
# returnRwRFMN plot autocorrelations
tmpRwRFMPlusNN = acf(returnRwRFMPlusNN,
                     main = "Autokorelacja wyników symulacji modelu 
           b³adzenia losowego (wed³ug FMPlusNN)")
# par(mfrow = c(1, 1))
dev.off()
```

```{r RwWithSP500, include=FALSE}
# r500 <- sp500.ret <- returns.z[,"sp500"]
# length(sp500.ret)
# rFMtoSP500 <- rFM[ (length(rFM)-length(returns.mat[,"sp500"])+1) : 
#                            length(rFM) ]
# rFMNNtoSP500 <- rFMNN[ (length(rFMNN)-length(returns.mat[,"sp500"])+1) : 
#                                length(rFMNN) ]
# rFMPlusNNtoSP500 <- rFMPlusNN[ (length(rFMPlusNN)-
#                                         length(returns.mat[,"sp500"])+1) : 
#                                        length(rFMPlusNN) ]
# rRWtoSP500 <- returnRwRFM[ (length(returnRwRFM)-
#                                     length(returns.mat[,"sp500"])+1) : 
#                                    length(returnRwRFM) ]
# rRWNNtoSP500 <- returnRwRFMNN[ (length(returnRwRFMNN)-
#                                         length(returns.mat[,"sp500"])+1) : 
#                                        length(returnRwRFMNN) ]
# rRWPlusNNtoSP500 <- returnRwRFMPlusNN[ (length(returnRwRFMPlusNN)-
#                                                 length(returns.mat[,"sp500"])+
#                                                 1) : length(returnRwRFMPlusNN) ]
# 
# length( rFMtoSP500 )
# length( rFMNNtoSP500 )
# length( rFMPlusNNtoSP500 )
# length( rRWtoSP500 )
# length( rRWNNtoSP500 )
# length( rRWPlusNNtoSP500 )
# 
# rAll <- cbind(rFMtoSP500, rFMNNtoSP500, rFMPlusNNtoSP500, rRWtoSP500, 
#               rRWNNtoSP500, rRWPlusNNtoSP500 )
# 
# 
# par(mfrow=c(6,1))
# par(mar = c(2,2,2,2))
# plot(rAll[, "rFMtoSP500"],col="blue", lwd=2, main="rFMtoSP500" )
# abline(h=0)
# plot(rAll[, "rFMNNtoSP500"],col="blue", lwd=2, main="rFMNNtoSP500" )
# abline(h=0)
# plot(rAll[, "rFMPlusNNtoSP500"],col="blue", lwd=2, main="rFMPlusNNtoSP500" )
# abline(h=0)
# plot(rAll[, "rRWtoSP500"],col="blue", lwd=2, main="rRWtoSP500" )
# abline(h=0)
# plot(rAll[, "rRWNNtoSP500"],col="blue", lwd=2, main="rRWNNtoSP500" )
# abline(h=0)
# plot(rAll[, "rRWPlusNNtoSP500"],col="blue", lwd=2, main="rRWPlusNNtoSP500" )
# abline(h=0)
# dev.off()
# 
# pairs(rAll, col="blue")
````