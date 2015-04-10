package com.github.rjeschke.txtmark;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class TableTest
{
    private static final Configuration EXTENDED_MODE =
            Configuration.builder().forceExtentedProfile().build();

    @Test
    public void ignoresTableInStandardsMode()
    {
        String input = "a|b|c\n-|-|-";
        String output = Processor.process(input);
        assertEquals("<p>" + input + "</p>\n", output);
    }

    @Test
    public void parseTableWithOnlyAHeader()
    {
        assertEquals(
                lines(
                        "<table>",
                        "<thead>",
                        "<tr><th>a</th><th>b</th><th>c</th></tr>",
                        "</thead>",
                        "</table>"),
                process("a|b|c\n-|-|-"));
    }

    @Test
    public void requiresBlankLineBeforeTable()
    {
        assertEquals(
                lines(
                        "<p>paragraph line",
                        "a|b",
                        "-|-</p>",
                        "<table>",
                        "<thead>",
                        "<tr><th>c</th><th>d</th></tr>",
                        "</thead>",
                        "</table>"),
                process(
                        "paragraph line",
                        "a|b",
                        "-|-",
                        "",
                        "c|d",
                        "-|-"));
    }

    @Test
    public void doesNotRequireLinesToHaveEqualNumberCells()
    {
        assertEquals(
                lines(
                        "<table>",
                        "<thead>",
                        "<tr><th>a</th><th>b</th></tr>",
                        "</thead>",
                        "<tbody>",
                        "<tr><td>c</td><td>d</td><td>e</td></tr>",
                        "</tbody>",
                        "</table>"),
                process(
                        "a|b",
                        "-|-",
                        "c|d|e"));
    }

    @Test
    public void canHaveInlineMarkdownWithinTableCell()
    {
        assertEquals(
                lines(
                        "<table>",
                        "<thead>",
                        "<tr><th>a</th><th>b</th></tr>",
                        "</thead>",
                        "<tbody>",
                        "<tr><td><em>c</em></td><td><strong>d</strong></td></tr>",
                        "</tbody>",
                        "</table>"),
                process(
                        "a|b",
                        "-|-",
                        "*c*|__d__"));
    }

    @Test
    public void canHaveLinkWithinTableCell()
    {
        assertEquals(
                lines(
                        "<table>",
                        "<thead>",
                        "<tr><th>a</th><th>b</th></tr>",
                        "</thead>",
                        "<tbody>",
                        "<tr><td><a href=\"bar\">foo</a></td><td></td></tr>",
                        "</tbody>",
                        "</table>"),
                process(
                        "a|b",
                        "-|-",
                        "[foo](bar)|"));
    }

    @Test
    public void handlesAsciiArtTable()
    {
        assertEquals(
                lines(
                        "<table>",
                        "<thead>",
                        "<tr><th> fruit </th><th> color</th></tr>",
                        "</thead>",
                        "<tbody>",
                        "<tr><td>apple  </td><td> red</td></tr>",
                        "<tr><td>orange </td><td> orange</td></tr>",
                        "</tbody>",
                        "</table>"),
                process(
                        " fruit | color",
                        "------ | ------",
                        "apple  | red",
                        "orange | orange"));
    }

    @Test
    public void canHaveLeadingPipe()
    {
        assertEquals(
                lines(
                        "<table>",
                        "<thead>",
                        "<tr><th> fruit  </th><th> color</th></tr>",
                        "</thead>",
                        "<tbody>",
                        "<tr><td> apple  </td><td> red</td></tr>",
                        "<tr><td> orange </td><td> orange</td></tr>",
                        "</tbody>",
                        "</table>"),
                process(
                        "| fruit  | color",
                        "| ------ | ------",
                        "| apple  | red",
                        "| orange | orange"));
    }

    @Test
    public void canHaveTrailingPipe()
    {
        assertEquals(
                lines(
                        "<table>",
                        "<thead>",
                        "<tr><th>fruit  </th><th> color  </th></tr>",
                        "</thead>",
                        "<tbody>",
                        "<tr><td>apple  </td><td> red    </td></tr>",
                        "<tr><td>orange </td><td> orange </td></tr>",
                        "</tbody>",
                        "</table>"),
                process(
                        "fruit  | color  |",
                        "------ | ------ |",
                        "apple  | red    |",
                        "orange | orange |"));
    }

    @Test
    public void tableWithEnclosingPipes()
    {
        assertEquals(
                lines(
                        "<table>",
                        "<thead>",
                        "<tr><th> fruit  </th><th> color  </th></tr>",
                        "</thead>",
                        "<tbody>",
                        "<tr><td> apple  </td><td> red    </td></tr>",
                        "<tr><td> orange </td><td> orange </td></tr>",
                        "</tbody>",
                        "</table>"),
                process(
                        "| fruit  | color  |",
                        "| ------ | ------ |",
                        "| apple  | red    |",
                        "| orange | orange |"));
    }

    @Test
    public void pipesDoNotHaveToAlign()
    {
        assertEquals(
                lines(
                        "<table>",
                        "<thead>",
                        "<tr><th>fruit</th><th>color</th></tr>",
                        "</thead>",
                        "<tbody>",
                        "<tr><td>apple</td><td>red</td></tr>",
                        "<tr><td>orange</td><td>orange</td></tr>",
                        "</tbody>",
                        "</table>"),
                process(
                        "fruit|color",
                        "-|-------",
                        "apple|red",
                        "orange|orange"));
    }

    private static String process(String... lines)
    {
        return Processor.process(lines(lines), EXTENDED_MODE);
    }

    private static String lines(String... lines)
    {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < lines.length; i++)
        {
            out.append(lines[i]);
            if (i + 1 < lines.length)
            {
                out.append('\n');
            }
        }
        return out.toString();
    }
}
