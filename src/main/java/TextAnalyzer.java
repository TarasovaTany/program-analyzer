import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class TextAnalyzer {
    private static final String LETTERS = "abc";
    private static final int TEXT_LENGTH = 100_000;
    private static final int TEXT_COUNT = 10_000;
    private static final int QUEUE_CAPACITY = 100;

    private static final BlockingQueue<String> queueA = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    private static final BlockingQueue<String> queueB = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    private static final BlockingQueue<String> queueC = new ArrayBlockingQueue<>(QUEUE_CAPACITY);

    public static void main(String[] args) throws InterruptedException {
        // Поток для генерации текстов
        Thread textGenerator = new Thread(() -> {
            for (int i = 0; i < TEXT_COUNT; i++) {
                String text = generateText(LETTERS, TEXT_LENGTH);
                try {
                    queueA.put(text);
                    queueB.put(text);
                    queueC.put(text);
                } catch (InterruptedException e) {
                    System.out.println("Поток генерации прерван.");
                    Thread.currentThread().interrupt();
                }
            }
        });
        textGenerator.start();

        // Поток для анализа символа 'a'
        Thread analyzerA = new Thread(() -> {
            int maxCount = 0;
            String maxText = "";
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    String text = queueA.take();
                    int count = countOccurrences(text, 'a');
                    if (count > maxCount) {
                        maxCount = count;
                        maxText = text;
                    }
                } catch (InterruptedException e) {
                    System.out.println("Поток анализа 'a' прерван.");
                    Thread.currentThread().interrupt();
                }
            }
            System.out.println("Максимальное количество 'a': " + maxCount + ", текст: " + maxText);
        });
        analyzerA.start();

        // Поток для анализа символа 'b'
        Thread analyzerB = new Thread(() -> {
            int maxCount = 0;
            String maxText = "";
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    String text = queueB.take();
                    int count = countOccurrences(text, 'b');
                    if (count > maxCount) {
                        maxCount = count;
                        maxText = text;
                    }
                } catch (InterruptedException e) {
                    System.out.println("Поток анализа 'b' прерван.");
                    Thread.currentThread().interrupt();
                }
            }
            System.out.println("Максимальное количество 'b': " + maxCount + ", текст: " + maxText);
        });
        analyzerB.start();

        // Поток для анализа символа 'c'
        Thread analyzerC = new Thread(() -> {
            int maxCount = 0;
            String maxText = "";
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    String text = queueC.take();
                    int count = countOccurrences(text, 'c');
                    if (count > maxCount) {
                        maxCount = count;
                        maxText = text;
                    }
                } catch (InterruptedException e) {
                    System.out.println("Поток анализа 'c' прерван.");
                    Thread.currentThread().interrupt();
                }
            }
            System.out.println("Максимальное количество 'c': " + maxCount + ", текст: " + maxText);
        });
        analyzerC.start();

        // Ожидание завершения генерации текстов
        textGenerator.join();

        // Прерывание потоков анализа
        analyzerA.interrupt();
        analyzerB.interrupt();
        analyzerC.interrupt();

        // Ожидание завершения потоков анализа
        analyzerA.join();
        analyzerB.join();
        analyzerC.join();
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static int countOccurrences(String text, char character) {
        int count = 0;
        for (char c : text.toCharArray()) {
            if (c == character) {
                count++;
            }
        }
        return count;
    }
}
