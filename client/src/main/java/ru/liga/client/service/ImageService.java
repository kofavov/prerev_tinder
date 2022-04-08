package ru.liga.client.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.liga.client.entity.User;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ImageService {

    @Value("${upload.path}")
    private String uploadPath;

    public File getFile(User user) {
        File result = null;
        try {
            File backgroundImage = new File(uploadPath);
            BufferedImage image = ImageIO.read(backgroundImage);
            Font header = new Font("Old Standard TT", Font.BOLD, 44);
            Font body = new Font("Old Standard TT", Font.PLAIN, 30);
            int leftOffset = (int) (image.getWidth() * 0.1);
            int topOffset = (int) (image.getHeight() * 0.1) + (header.getSize() / 2);
            int maxLineWidth = image.getWidth() - 2 * leftOffset;
            log.info("leftOffset: {}, topOffset: {}, maxLineWidth: {}", leftOffset, topOffset, maxLineWidth);
            Graphics g = image.getGraphics();
            g.setColor(Color.BLACK);
            g.setFont(header);
            List<String> linesToWrite = getLinesToWrite(user, g, maxLineWidth, header, body);
            writeLinesToImage(body, leftOffset, topOffset, g, linesToWrite);
            result = new File(backgroundImage.getParentFile(), "result_image.jpg");
            ImageIO.write(image, "jpg", result);
        } catch (IOException e) {
            log.info(e.getMessage());
        }
        return result;
    }

    private void writeLinesToImage(Font body, int leftOffset, int topOffset, Graphics g, List<String> linesToWrite) {
        FontMetrics fm = g.getFontMetrics();
        if (linesToWrite.size() == 1) {
            String[] words = linesToWrite.get(0).split("\\s");
            g.drawString(words[0], leftOffset, topOffset);
            String descLine = Arrays.stream(words).skip(1).collect(Collectors.joining(" "));
            g.setFont(body);
            fm = g.getFontMetrics();
            topOffset += fm.getHeight();
            g.drawString(descLine, leftOffset, topOffset);
        } else {
            for (int i = 0; i < linesToWrite.size(); i++) {
                if (i == 0) {
                    g.drawString(linesToWrite.get(i), leftOffset, topOffset);
                    g.setFont(body);
                    fm = g.getFontMetrics();
                } else {
                    g.drawString(linesToWrite.get(i), leftOffset, topOffset);
                }
                topOffset += fm.getHeight();
            }
        }
    }

    private List<String> getLinesToWrite(User user, Graphics graphics, int maxLineWidth, Font header, Font body) {
        FontMetrics fontMetrics = graphics.getFontMetrics(header);
        List<String> linesToWrite = new ArrayList<>();
        String head = user.getHeading();
        String heading = head.length()<20 ? head
                : head.replace(".", "")
                .replaceAll("\\s.*", "");
        linesToWrite.add(heading);
        String preDescript = head.replaceAll(heading + "\\S*\\s?", "") + " " + user.getDescription();
        String[] description = preDescript.split("\\s");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < description.length; ) {
            sb.append(description[i++]);
            while ((i < description.length) && (fontMetrics.stringWidth(sb + " " + description[i]) < maxLineWidth)) {
                sb.append(" ");
                sb.append(description[i++]);
            }
            if (fontMetrics.getFont().equals(header)) {
                fontMetrics = graphics.getFontMetrics(body);
            }
            linesToWrite.add(sb.toString());
            sb.setLength(0);
        }
        return linesToWrite;
    }
}
