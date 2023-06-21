import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BooleanSearchEngineOptional extends BooleanSearchEngine {

    private List<String> stopList;
    public BooleanSearchEngineOptional(File pdfsDir) throws IOException {
        super(pdfsDir);
        stopList = readStopList(new File("stop-ru.txt"));
    }

    @Override
    public List<PageEntry> search(String request) {
        // тут реализуем поиск по списку слов
        List<PageEntry> result = new ArrayList<>();

        var requestWords = request.split("\\P{IsAlphabetic}+");

        for (var requestWord : requestWords) { // перебираем слова запроса
            if (requestWord.isEmpty() || stopList.contains(requestWord)) {
                continue;
            }
            requestWord = requestWord.toLowerCase();
            List<PageEntry>  oneWordEntries = new ArrayList<>(index.get(requestWord));

            result = new ArrayList<>(mergeEntries(result, oneWordEntries));

        }
        return result;
    }

    private List<PageEntry> mergeEntries(List<PageEntry> origin, List<PageEntry> added) {
        List<PageEntry> result;

        if (origin.isEmpty())  {
            result = new ArrayList<>(added);
            return result;
        }

        result = new ArrayList<>();

        for (PageEntry originPageEntry : origin) {
            for (PageEntry addedPageEntry : added) {
                int count = originPageEntry.getCount();
                if (originPageEntry.getPdfName().equals(addedPageEntry.getPdfName()) &&
                        originPageEntry.getPage() == addedPageEntry.getPage()) {
                    count += addedPageEntry.getCount();
                }
                result.add(new PageEntry(originPageEntry.getPdfName(),
                        originPageEntry.getPage(),
                        count));
            }
        }

        return result;
    }

    private List<String> readStopList(File file) {
        List<String> result = new ArrayList<>();
        try (BufferedReader buffReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = buffReader.readLine()) != null) {
                if (line.isEmpty()) {
                    break;
                }
                result.add(line);
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
