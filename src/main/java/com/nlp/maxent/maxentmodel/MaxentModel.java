package com.nlp.maxent.maxentmodel;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizer;
import opennlp.tools.doccat.DocumentCategorizerME;

import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class MaxentModel {

    public static final String ModelFileName = "en-so-classifier-maxent.bin";
    public static final String TestDataFileName = "test-data.txt";
    public static final String TrainDataFileName = "train-data.txt";

    public MaxentModel() {
    }

    public String evalTestData() {
        DoccatModel model = readModel(ModelFileName);
        List<List<String>> testData = readTestData(TestDataFileName);
        String result = "";
        int testDataCount = testData.size();
        int successfulPredictions = 0;
        for (List<String> testDatum: testData) {
            String originalTag = testDatum.get(0);
            String predictedTag = evaluateModel(model, testDatum.get(1));
            result += "originalTag: " + originalTag + ", predictedTag: " + predictedTag + "\n";
            if (predictedTag.equals(originalTag))
                successfulPredictions++;
        }
        result += "\n" + successfulPredictions + " out of " + testDataCount + " successful predictions";
        return result;
    }

    public List<List<String>> readTestData(String testDataFileName) {
        InputStream ins = null; // raw byte-stream
        Reader r = null; // cooked reader
        BufferedReader br = null; // buffered for readLine()
        List<List<String>> testDataArr = new ArrayList<List<String>>();
        try {
            String s;
            ClassPathResource resource = new ClassPathResource(testDataFileName);
            File file = resource.getFile();
            ins = new FileInputStream(file);
            r = new InputStreamReader(ins, "UTF-8"); // leave charset out for default
            br = new BufferedReader(r);
            while ((s = br.readLine()) != null) {
                testDataArr.add(Arrays.asList(s.split("\t")));
            }
            return testDataArr;
        }
        catch (Exception e) {
            System.err.println(e.getMessage()); // handle exception
            return null;
        }
        finally {
            if (br != null) { try { br.close(); } catch(Throwable t) { /* ensure close happens */ } }
            if (r != null) { try { r.close(); } catch(Throwable t) { /* ensure close happens */ } }
            if (ins != null) { try { ins.close(); } catch(Throwable t) { /* ensure close happens */ } }
        }
    }

    public String trainModel(String trainingDataFile) {
        try {
            ClassPathResource resource = new ClassPathResource(trainingDataFile);
            File trainDataFile = resource.getFile();

            // read the training data
            InputStreamFactory dataIn = new MarkableFileInputStreamFactory(trainDataFile);
            ObjectStream lineStream = new PlainTextByLineStream(dataIn, "UTF-8");
            ObjectStream sampleStream = new DocumentSampleStream(lineStream);

            // define the training parameters
            TrainingParameters params = new TrainingParameters();
            params.put(TrainingParameters.ITERATIONS_PARAM, 10 + "");
            params.put(TrainingParameters.CUTOFF_PARAM, 0 + "");

            // create a model from traning data
            DoccatModel model = DocumentCategorizerME.train("en", sampleStream, params, new DoccatFactory());
            System.out.println("\nModel is successfully trained.");

            // save the model to local
            ClassLoader classLoader = getClass().getClassLoader();
            String modelFileName = classLoader.getResource(".").getFile() + File.separator + ModelFileName;
            modelFileName =  java.net.URLDecoder.decode(modelFileName, "UTF-8");
            File modelFile = new File(modelFileName);
//            modelFile.createNewFile();
            BufferedOutputStream modelOut = new BufferedOutputStream(new FileOutputStream(modelFile));
            model.serialize(modelOut);
            System.out.println("\nTrained Model is saved locally at : "+modelFile.getPath());
            return "model successfully trained";
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            return "failed to train model";
        }
    }

    public String evaluateModel(DoccatModel model, String testData) {
        // test the model file by subjecting it to prediction
        DocumentCategorizer doccat = new DocumentCategorizerME(model);
        String[] docWords = testData.replaceAll("[^A-Za-z]", " ").split(" ");
        double[] aProbs = doccat.categorize(docWords);

        // print the probabilities of the categories
        /*System.out.println("\n---------------------------------\nCategory : Probability\n---------------------------------");
        for(int i=0;i<doccat.getNumberOfCategories();i++){
            System.out.println(doccat.getCategory(i)+" : "+aProbs[i]);
        }
        System.out.println("---------------------------------");

        System.out.println("\n"+doccat.getBestCategory(aProbs)+" : is the predicted category for the given sentence.");*/
        return doccat.getBestCategory(aProbs);
    }

    public DoccatModel readModel(String modelFileName) {
        InputStream modelIn = null;
//        "en-so-classifier-maxent.bin"
        try {
            ClassPathResource resource = new ClassPathResource(modelFileName);
            File file = resource.getFile();
            modelIn = new FileInputStream(file);
            return new DoccatModel(modelIn);
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        finally {
            if (modelIn != null) {
                try {
                    modelIn.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
