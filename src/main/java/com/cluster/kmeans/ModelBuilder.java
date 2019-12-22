package com.cluster.kmeans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ModelBuilder {
    @Autowired
    DataSource dataSource;
    @Autowired
    ApplicationUtil helper;

    @Autowired
    ApplicationProperties properties;

    public Model convertToModel(String[] values) {
        Model model = new Model();

        for (int i = 0; i < properties.variableAt.size(); i++) {
            String indexString = properties.variableAt.get(i);

            try {
                model.id = Long.valueOf(values[1]);
            } catch (Exception e) {
                System.err.println("Id parsing failed");
                System.err.println(e.getMessage());
                return null;
            }

            switch (indexString) {
                case "0":
                    try {
                        model.indexNo = Long.valueOf(values[0]);
                    } catch (Exception e) {
                        System.err.println("Index parsing failed");
                        System.err.println(e.getMessage());
                        return null;
                    }
                    break;
                case "1":
                    //already set id value;
                    break;
                case "3":
                    model.publication = helper.computeWordWeight(values[3]);
                    break;
                case "4":
                    model.author = helper.computeWordWeight(values[4]);
                    break;
                case "5":
                    String date = values[5];
                    date = date.replaceAll("-", "")
                            .replaceAll("\\s", "");

                    try {
                        model.date = Long.valueOf(date);
                    } catch (Exception e) {
                        // wrongDataCount ++;
                        return null;
                    }
                    break;
                case "6":
                    String year = values[6];
                    model.year = Long.valueOf(year);
                    ;
                    break;
                case "7":
                    String month = values[7];
                    model.month = Long.valueOf(month);
                    ;
                    break;
                default:
                    break;
            }
        }

        return model;
    }

    public Long[] convertToArray(String[] values) {
        Long[] record = new Long[properties.variableAt.size()];
        for (int i = 0; i < values.length; i++) {
            record[i] = Long.valueOf(values[i]);
        }
        return record;
    }
}
