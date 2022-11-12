package org.immersed.d2r.mod;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.immersed.d2r.model.CascDatabase;
import org.immersed.d2r.model.CascFile;

public class CsvModFile implements ModFile
{
    private final Mod mod;
    private final CascFile item;
    private final List<String[]> rows;

    public CsvModFile(Mod mod, CascFile item) throws IOException
    {
        this.mod = mod;
        this.item = item;

        CascDatabase database = mod.database();

        byte[] data = item.getFileBytes(database);

        String[] rowsAsText = new String(data).split(System.getProperty("line.separator"));
        this.rows = new ArrayList<>(rowsAsText.length);

        final char tab = '\t';

        for (String row : rowsAsText)
        {
            int size = (int) row.chars()
                                .filter(i -> i == tab)
                                .count();

            final String[] rowData = new String[size];

            int start = 0;
            int next = 0;

            for (int i = 0; i < size; i++)
            {
                next = row.indexOf(tab, start);
                rowData[i] = row.substring(start, next);
                start = next + 1;
            }

            this.rows.add(rowData);
        }
    }

    public String[] getHeaders()
    {
        return rows.get(0);
    }

    public List<String[]> getRows()
    {
        return rows.subList(1, rows.size());
    }

    public int getRowCount()
    {
        return getRows().size();
    }

    public String getValueAt(String column, int row)
    {
        String[] headers = getHeaders();

        for (int i = 0; i < headers.length; i++)
        {
            String header = headers[i];

            if (header.equalsIgnoreCase(column))
            {
                return getRows().get(row)[i];
            }
        }

        return "";
    }

    @Override
    public void save(Path dataRoot)
    {
        Path modFile = dataRoot.resolve(item.relativePath());

        try (BufferedWriter writer = Files.newBufferedWriter(modFile, StandardOpenOption.CREATE,
                StandardOpenOption.WRITE))
        {
            for (String[] row : this.rows)
            {
                String rowText = Arrays.stream(row)
                                       .reduce((s1, s2) -> s1 + "\\t" + s2)
                                       .orElse("");

                writer.write(rowText);
                writer.newLine();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
