package com.soilanalysis.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.soilanalysis.model.SoilAnalysis;
import com.soilanalysis.model.SoilAnalysis.FertilizerRecommendation;
import com.soilanalysis.model.SoilAnalysis.CropSuitability;
import com.soilanalysis.model.SoilAnalysis.EnvironmentalImpact;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import javax.imageio.ImageIO;
import com.itextpdf.io.image.ImageDataFactory;

public class ReportGeneratorService {
    private static final DeviceRgb SOIL_GREEN = new DeviceRgb(79, 121, 66);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM dd, yyyy");

    public void generateReport(SoilAnalysis analysis, String outputPath) throws IOException {
        PdfWriter writer = new PdfWriter(new FileOutputStream(outputPath));
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Add title page
        addTitlePage(document, analysis);
        
        // Add soil health overview
        addSoilHealthOverview(document, analysis);
        
        // Add nutrient analysis
        addNutrientAnalysis(document, analysis);
        
        // Add micronutrient analysis
        addMicronutrientAnalysis(document, analysis);
        
        // Add crop suitability analysis
        addCropSuitabilityAnalysis(document, analysis);
        
        // Add environmental impact analysis
        addEnvironmentalImpactAnalysis(document, analysis);
        
        // Add seasonal recommendations
        addSeasonalRecommendations(document, analysis);
        
        // Add recommendations
        addRecommendations(document, analysis);
        
        // Add improvement areas
        addImprovementAreas(document, analysis);
        
        // Add charts
        addCharts(document, analysis);

        document.close();
    }

    private void addTitlePage(Document document, SoilAnalysis analysis) {
        Paragraph title = new Paragraph("Soil Analysis Report")
            .setFontSize(24)
            .setBold()
            .setTextAlignment(TextAlignment.CENTER)
            .setFontColor(SOIL_GREEN)
            .setMarginBottom(20);

        Paragraph subtitle = new Paragraph("Comprehensive Soil Health Assessment")
            .setFontSize(16)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(40);

        Table infoTable = new Table(2)
            .setWidth(UnitValue.createPercentValue(80))
            .setMarginBottom(20)
            .setHorizontalAlignment(HorizontalAlignment.CENTER);

        infoTable.addCell(createCell("Report ID:", true));
        infoTable.addCell(createCell(analysis.getId(), false));
        infoTable.addCell(createCell("Analysis Date:", true));
        infoTable.addCell(createCell(analysis.getSoilData().getAnalysisDate().format(DATE_FORMATTER), false));
        infoTable.addCell(createCell("Location:", true));
        infoTable.addCell(createCell(analysis.getSoilData().getLocation(), false));
        infoTable.addCell(createCell("Soil Type:", true));
        infoTable.addCell(createCell(analysis.getSoilData().getSoilType().toString(), false));

        document.add(title);
        document.add(subtitle);
        document.add(infoTable);
        document.add(new AreaBreak());
    }

    private void addSoilHealthOverview(Document document, SoilAnalysis analysis) {
        Paragraph heading = new Paragraph("Soil Health Overview")
            .setFontSize(18)
            .setBold()
            .setFontColor(SOIL_GREEN)
            .setMarginBottom(10);

        Paragraph score = new Paragraph(String.format("Overall Health Score: %.1f/10", analysis.getHealthScore()))
            .setFontSize(16)
            .setBold()
            .setMarginBottom(10);

        Paragraph assessment = new Paragraph(analysis.getOverallAssessment())
            .setFontSize(12)
            .setMarginBottom(20);

        document.add(heading);
        document.add(score);
        document.add(assessment);
        document.add(new AreaBreak());
    }

    private void addNutrientAnalysis(Document document, SoilAnalysis analysis) {
        Paragraph heading = new Paragraph("Nutrient Analysis")
            .setFontSize(18)
            .setBold()
            .setFontColor(SOIL_GREEN)
            .setMarginBottom(20);

        Table nutrientTable = new Table(2)
            .setWidth(UnitValue.createPercentValue(100))
            .setMarginBottom(20);

        nutrientTable.addCell(createCell("pH Level", true));
        nutrientTable.addCell(createCell(String.format("%.1f", analysis.getSoilData().getPh()), false));
        nutrientTable.addCell(createCell("Nitrogen (ppm)", true));
        nutrientTable.addCell(createCell(String.format("%.1f", analysis.getSoilData().getNitrogen()), false));
        nutrientTable.addCell(createCell("Phosphorus (ppm)", true));
        nutrientTable.addCell(createCell(String.format("%.1f", analysis.getSoilData().getPhosphorus()), false));
        nutrientTable.addCell(createCell("Potassium (ppm)", true));
        nutrientTable.addCell(createCell(String.format("%.1f", analysis.getSoilData().getPotassium()), false));
        nutrientTable.addCell(createCell("Organic Matter (%)", true));
        nutrientTable.addCell(createCell(String.format("%.1f", analysis.getSoilData().getOrganicMatter()), false));
        nutrientTable.addCell(createCell("Moisture (%)", true));
        nutrientTable.addCell(createCell(String.format("%.1f", analysis.getSoilData().getMoisture()), false));

        document.add(heading);
        document.add(nutrientTable);
        document.add(new AreaBreak());
    }

    private void addMicronutrientAnalysis(Document document, SoilAnalysis analysis) {
        Paragraph heading = new Paragraph("Micronutrient Analysis")
            .setFontSize(18)
            .setBold()
            .setFontColor(SOIL_GREEN)
            .setMarginBottom(20);

        Table micronutrientTable = new Table(2)
            .setWidth(UnitValue.createPercentValue(100))
            .setMarginBottom(20);

        micronutrientTable.addCell(createCell("Iron (ppm)", true));
        micronutrientTable.addCell(createCell(String.format("%.1f", analysis.getSoilData().getIron()), false));
        micronutrientTable.addCell(createCell("Zinc (ppm)", true));
        micronutrientTable.addCell(createCell(String.format("%.1f", analysis.getSoilData().getZinc()), false));
        micronutrientTable.addCell(createCell("Copper (ppm)", true));
        micronutrientTable.addCell(createCell(String.format("%.1f", analysis.getSoilData().getCopper()), false));
        micronutrientTable.addCell(createCell("Manganese (ppm)", true));
        micronutrientTable.addCell(createCell(String.format("%.1f", analysis.getSoilData().getManganese()), false));

        // Add nutrient scores
        Paragraph scoresHeading = new Paragraph("Nutrient Scores")
            .setFontSize(14)
            .setBold()
            .setMarginTop(20)
            .setMarginBottom(10);

        Table scoresTable = new Table(2)
            .setWidth(UnitValue.createPercentValue(100))
            .setMarginBottom(20);

        for (Map.Entry<String, Double> entry : analysis.getNutrientScores().entrySet()) {
            scoresTable.addCell(createCell(entry.getKey(), true));
            scoresTable.addCell(createCell(String.format("%.1f/10", entry.getValue()), false));
        }

        document.add(heading);
        document.add(micronutrientTable);
        document.add(scoresHeading);
        document.add(scoresTable);
        document.add(new AreaBreak());
    }

    private void addCropSuitabilityAnalysis(Document document, SoilAnalysis analysis) {
        Paragraph heading = new Paragraph("Crop Suitability Analysis")
            .setFontSize(18)
            .setBold()
            .setFontColor(SOIL_GREEN)
            .setMarginBottom(20);

        for (CropSuitability crop : analysis.getCropSuitability()) {
            Table cropTable = new Table(1)
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(15);

            Cell headerCell = new Cell()
                .setBackgroundColor(SOIL_GREEN)
                .setFontColor(ColorConstants.WHITE)
                .setPadding(10)
                .add(new Paragraph(crop.getCropName() + 
                    String.format(" (Suitability Score: %.1f/10)", crop.getSuitabilityScore()))
                    .setBold());

            Cell contentCell = new Cell()
                .setPadding(10);

            // Add advantages
            if (!crop.getAdvantages().isEmpty()) {
                contentCell.add(new Paragraph("Advantages:").setBold());
                List advantagesList = new List()
                    .setSymbolIndent(12)
                    .setListSymbol("✓")
                    .setFontSize(12);
                for (String advantage : crop.getAdvantages()) {
                    advantagesList.add(new ListItem(advantage));
                }
                contentCell.add(advantagesList);
            }

            // Add challenges
            if (!crop.getChallenges().isEmpty()) {
                contentCell.add(new Paragraph("Challenges:").setBold());
                List challengesList = new List()
                    .setSymbolIndent(12)
                    .setListSymbol("!")
                    .setFontSize(12);
                for (String challenge : crop.getChallenges()) {
                    challengesList.add(new ListItem(challenge));
                }
                contentCell.add(challengesList);
            }

            // Add recommendations
            contentCell.add(new Paragraph("Recommended Variety: " + crop.getRecommendedVariety()));
            contentCell.add(new Paragraph("Best Planting Season: " + crop.getPlantingSeason()));

            cropTable.addCell(headerCell);
            cropTable.addCell(contentCell);
            document.add(cropTable);
        }

        document.add(new AreaBreak());
    }

    private void addEnvironmentalImpactAnalysis(Document document, SoilAnalysis analysis) {
        Paragraph heading = new Paragraph("Environmental Impact Analysis")
            .setFontSize(18)
            .setBold()
            .setFontColor(SOIL_GREEN)
            .setMarginBottom(20);

        for (EnvironmentalImpact impact : analysis.getEnvironmentalImpacts()) {
            Table impactTable = new Table(1)
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(15);

            Cell headerCell = new Cell()
                .setBackgroundColor(SOIL_GREEN)
                .setFontColor(ColorConstants.WHITE)
                .setPadding(10)
                .add(new Paragraph(impact.getImpactType() + 
                    " (Severity: " + impact.getSeverity() + ")")
                    .setBold());

            Cell contentCell = new Cell()
                .setPadding(10)
                .add(new Paragraph(impact.getDescription()))
                .add(new Paragraph("Long-term Effect: " + impact.getLongTermEffect()));

            // Add mitigation strategies
            if (!impact.getMitigationStrategies().isEmpty()) {
                contentCell.add(new Paragraph("Mitigation Strategies:").setBold());
                List strategiesList = new List()
                    .setSymbolIndent(12)
                    .setListSymbol("→")
                    .setFontSize(12);
                for (String strategy : impact.getMitigationStrategies()) {
                    strategiesList.add(new ListItem(strategy));
                }
                contentCell.add(strategiesList);
            }

            impactTable.addCell(headerCell);
            impactTable.addCell(contentCell);
            document.add(impactTable);
        }

        document.add(new AreaBreak());
    }

    private void addSeasonalRecommendations(Document document, SoilAnalysis analysis) {
        Paragraph heading = new Paragraph("Seasonal Recommendations")
            .setFontSize(18)
            .setBold()
            .setFontColor(SOIL_GREEN)
            .setMarginBottom(20);

        Table seasonalTable = new Table(2)
            .setWidth(UnitValue.createPercentValue(100))
            .setMarginBottom(20);

        for (Map.Entry<String, String> entry : analysis.getSeasonalRecommendations().entrySet()) {
            seasonalTable.addCell(createCell(entry.getKey(), true));
            seasonalTable.addCell(createCell(entry.getValue(), false));
        }

        document.add(heading);
        document.add(seasonalTable);
        document.add(new AreaBreak());
    }

    private void addRecommendations(Document document, SoilAnalysis analysis) {
        Paragraph heading = new Paragraph("Recommendations")
            .setFontSize(18)
            .setBold()
            .setFontColor(SOIL_GREEN)
            .setMarginBottom(20);

        for (FertilizerRecommendation rec : analysis.getRecommendations()) {
            Table recTable = new Table(1)
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(15);

            Cell headerCell = new Cell()
                .setBackgroundColor(SOIL_GREEN)
                .setFontColor(ColorConstants.WHITE)
                .setPadding(10)
                .add(new Paragraph(rec.getName()).setBold());

            Cell contentCell = new Cell()
                .setPadding(10)
                .add(new Paragraph("Amount: " + rec.getAmount()))
                .add(new Paragraph("Frequency: " + rec.getFrequency()))
                .add(new Paragraph("Benefits: " + rec.getBenefits()))
                .add(new Paragraph("Notes: " + rec.getNotes()));

            recTable.addCell(headerCell);
            recTable.addCell(contentCell);
            document.add(recTable);
        }

        document.add(new AreaBreak());
    }

    private void addImprovementAreas(Document document, SoilAnalysis analysis) {
        Paragraph heading = new Paragraph("Areas for Improvement")
            .setFontSize(18)
            .setBold()
            .setFontColor(SOIL_GREEN)
            .setMarginBottom(20);

        List list = new List()
            .setSymbolIndent(12)
            .setListSymbol("•")
            .setFontSize(12);

        for (String area : analysis.getImprovementAreas()) {
            list.add(new ListItem(area));
        }

        document.add(heading);
        document.add(list);
        document.add(new AreaBreak());
    }

    private void addCharts(Document document, SoilAnalysis analysis) throws IOException {
        Paragraph heading = new Paragraph("Visual Analysis")
            .setFontSize(18)
            .setBold()
            .setFontColor(SOIL_GREEN)
            .setMarginBottom(20);

        // Create nutrient bar chart
        DefaultCategoryDataset nutrientDataset = new DefaultCategoryDataset();
        nutrientDataset.addValue(analysis.getSoilData().getNitrogen(), "Level", "Nitrogen");
        nutrientDataset.addValue(analysis.getSoilData().getPhosphorus(), "Level", "Phosphorus");
        nutrientDataset.addValue(analysis.getSoilData().getPotassium(), "Level", "Potassium");
        nutrientDataset.addValue(analysis.getSoilData().getOrganicMatter() * 10, "Level", "Organic Matter");
        nutrientDataset.addValue(analysis.getSoilData().getIron(), "Level", "Iron");
        nutrientDataset.addValue(analysis.getSoilData().getZinc(), "Level", "Zinc");
        nutrientDataset.addValue(analysis.getSoilData().getCopper(), "Level", "Copper");
        nutrientDataset.addValue(analysis.getSoilData().getManganese(), "Level", "Manganese");

        JFreeChart nutrientChart = ChartFactory.createBarChart(
            "Nutrient Levels",
            "Nutrient",
            "PPM / %*10",
            nutrientDataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );

        // Create crop suitability chart
        DefaultCategoryDataset cropDataset = new DefaultCategoryDataset();
        for (CropSuitability crop : analysis.getCropSuitability()) {
            cropDataset.addValue(crop.getSuitabilityScore(), "Suitability", crop.getCropName());
        }

        JFreeChart cropChart = ChartFactory.createBarChart(
            "Crop Suitability Scores",
            "Crop",
            "Score",
            cropDataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );

        // Create health score pie chart
        DefaultPieDataset healthDataset = new DefaultPieDataset();
        healthDataset.setValue("Health Score", analysis.getHealthScore());
        healthDataset.setValue("Remaining", 10 - analysis.getHealthScore());

        JFreeChart healthChart = ChartFactory.createPieChart(
            "Soil Health Score",
            healthDataset,
            true,
            true,
            false
        );

        // Convert charts to images and add to PDF
        document.add(heading);
        document.add(new Paragraph("Nutrient Distribution").setBold());
        document.add(new Image(ImageDataFactory.create(chartToImage(nutrientChart))));
        document.add(new Paragraph("Crop Suitability").setBold());
        document.add(new Image(ImageDataFactory.create(chartToImage(cropChart))));
        document.add(new Paragraph("Health Score Distribution").setBold());
        document.add(new Image(ImageDataFactory.create(chartToImage(healthChart))));
    }

    private Cell createCell(String text, boolean isHeader) {
        Cell cell = new Cell().add(new Paragraph(text));
        if (isHeader) {
            cell.setBold();
            cell.setBackgroundColor(new DeviceRgb(240, 240, 240));
        }
        return cell;
    }

    private byte[] chartToImage(JFreeChart chart) throws IOException {
        BufferedImage image = chart.createBufferedImage(600, 400);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }
} 