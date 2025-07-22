# 📸 Screenshot Optimization for Extent Report Integration

## ✨ Purpose
To minimize the size of embedded screenshots in Extent Reports without sacrificing image quality — resulting in lighter, shareable reports that load faster and transmit seamlessly across pipelines and review platforms.

## ⚙️ How it works?
This utility captures browser screenshots, resizes them using high-quality bilinear interpolation, and converts them into base64-encoded JPEGs.The result: visually clear artifacts with dramatically reduced size — ideal for CI/CD storage, email attachments, and stakeholder consumption.

## 🌟 Highlights:

📉 Compact image size with visually intact results

💨 Faster report load times in local and pipeline environments

🔗 HTML-safe base64 embedding, no external image hosting needed

🛠️ Ideal for multi-scenario, image-heavy Extent Reports

## 🔍 Use Case
In Selenium-based test automation frameworks, screenshots are commonly embedded in failure logs and Extent Reports.The image size is influenced by browser resolution, UI complexity, and rendering artifacts. However,

- Raw TakesScreenshot images can be bulky (~200–600 KB per capture even with Base64 string type).

- Embedding large images inflates HTML report size and increases load time.

- CI/CD pipelines may struggle to render oversized reports with dozens of screenshots.

This utility streamlines the visual logging process by:

- Capturing a full-size image from the browser.

- Resizing it (optional scaleFactor) using smooth bilinear interpolation.

- Converting the image into a base64 string for seamless HTML embedding.

## 🧩 Code Breakdown

```java
// 1. Capture the screenshot
File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

// 2. Read it into BufferedImage
BufferedImage original = ImageIO.read(src);

// 3. Resize it using scale factor (1.0 = original size, 0.75 = 75%, 0.50 = 50%)
int width = (int)(original.getWidth() * scaleFactor);
int height = (int)(original.getHeight() * scaleFactor);
BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

// 4. Apply rendering hints for smooth scaling
Graphics2D g = resized.createGraphics();
g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
g.drawImage(original, 0, 0, width, height, null);
g.dispose();

// 5. Convert to base64 for embedding
ByteArrayOutputStream baos = new ByteArrayOutputStream();
ImageIO.write(resized, "jpg", baos); // Use "jpg" for compactness
String srcFile = Base64.getEncoder().encodeToString(baos.toByteArray());
```
Since we are reducing the image resolution and then converting to Base64, this approach takes less image size than default Base64 string, 

## 📘 Insight
By reducing resolution before encoding, this strategy trims the report size significantly **often by 40–70%**  with no perceptible degradation in visual clarity for report viewers. It’s especially impactful when hundreds of screenshots are embedded across test scenarios.