package com.skplanet.omp.knowledgeBuilding;

import com.skplanet.nlp.cli.CommandLineInterface;
import com.skplanet.nlp.utils.UtilTimer;
import com.skplanet.omp.knowledgeBuilding.ohash.OpinionNode_SentimentExpression;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

public class AspectExtractorTester {
    public static void main(String[] args) {

        CommandLineInterface cli = new CommandLineInterface();
        cli.addOption("d", "domain", true, "domain prefix", true);
        cli.addOption("i", "input", true, "input file path", true);
        cli.addOption("o", "output", true, "output path", true);
        cli.addOption("s", "stop", true, "stopwords.txt", true);
        cli.addOption("d", "sentiment", true, "sentiment dict", true);
        cli.parseOptions(args);

        try {
            long start_time = System.currentTimeMillis();

            AspectExtractor tester = new AspectExtractor();

            String PREFIX = "beauty";

            String dataDirectory = cli.getOption("i");
            String attrDirectoryFromClue = cli.getOption("o") + "/attr_" + PREFIX + "_clue.txt";
            String attrDirectoryFromExpr = cli.getOption("o") + "/attr_" + PREFIX + "_expr.txt";


            // 금칙어 setting
            tester.stopwords.addAll(tester.setWordList(cli.getOption("s")));

            // 감성사전 setting
            tester.sentimentDic.set_dict(cli.getOption("d"), tester.nlpapi);

            tester.sentimentDic.print_dict();

            Map<String, OpinionNode_SentimentExpression> exp = tester.sentimentDic.getAttributes().get(0).roots;

            for (Entry<String, OpinionNode_SentimentExpression> entry : exp.entrySet()) {
                System.out.println(entry.getKey() + ":" + entry.getValue().getExpressionValue());
            }

            File file = new File(dataDirectory);
            System.out.println(file.getName());
            tester.startAnalysis(tester, file, "UTF-8", "true", "true");
            tester.docs.clear();

            tester.saveAttrFromClue(attrDirectoryFromClue);
            tester.saveAttrFromExpr(attrDirectoryFromExpr);

            System.out.println("Used Memory : " + ((Runtime.getRuntime().totalMemory() - Runtime .getRuntime().freeMemory()) / 1024) / 1024 + "MB");
            long end_time = System.currentTimeMillis();
            System.out.println("Run time : " + UtilTimer.timeDiff(start_time, end_time));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
