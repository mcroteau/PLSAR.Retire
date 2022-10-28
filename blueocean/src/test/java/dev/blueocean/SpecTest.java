package dev.blueocean;

import dev.blueocean.model.PageCache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SpecTest extends BaseTest {

    @Test
    public void a() throws BlueOceanException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        PageCache pageCache = this.create();
        ExperienceManager exp = new ExperienceManager();
        StringBuilder sb = new StringBuilder();
        sb.append("<ocean:foreach items=\"${todos}\" var=\"tdo\">\n");
        sb.append("     <ocean:if spec=\"${tdo.title == 'Exercise *0'}\">\n");
        sb.append("         *ned.\n");
        sb.append("     </ocean:if>\n");
        sb.append("     <ocean:if spec=\"${tdo.title == 'Exercise *1'}\">\n");
        sb.append("         *jermaine.\n");
        sb.append("     </ocean:if>\n");
        sb.append("</ocean:foreach>\n");
        String result = exp.execute(sb.toString(), pageCache, null, new ArrayList<>()).replaceAll("([^\\S\\r\\n])+|(?:\\r?\\n)+", "");
        assertEquals("*ned.*jermaine.", result);
    }

    @Test
    public void b() throws BlueOceanException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        PageCache resp = this.create();
        ExperienceManager exp = new ExperienceManager();
        StringBuilder sb = new StringBuilder();
        sb.append("<ocean:if spec=\"${message != ''}\">\n");
        sb.append("${message}\n");
        sb.append("</ocean:if>\n");
        String result = exp.execute(sb.toString(), resp, null,new ArrayList<>()).trim();
        assertEquals("Strain.", result);
    }

    @Test
    public void c() throws BlueOceanException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        PageCache pageCache = this.create();
        StringBuilder sb = new StringBuilder();
        ExperienceManager exp = new ExperienceManager();
        sb.append("<ocean:if spec=\"${blank != ''}\">\n");
        sb.append("Nothing.\n");
        sb.append("</ocean:if>\n");
        String result = exp.execute(sb.toString(), pageCache, null,new ArrayList<>()).trim();
        assertEquals("", result);
    }

    @Test
    public void d() throws BlueOceanException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        PageCache pageCache = this.create();
        StringBuilder sb = new StringBuilder();
        ExperienceManager exp = new ExperienceManager();
        sb.append("<ocean:if spec=\"${tom.name == 'Tom'}\">\n");
        sb.append("Tom.\n");
        sb.append("</ocean:if>\n");
        sb.append("<ocean:if spec=\"${tom.wife.name == 'Penelope'}\">\n");
        sb.append("Penelope.\n");
        sb.append("</ocean:if>\n");
        sb.append("<ocean:if spec=\"${tom.wife.pet.name == 'Diego'}\">\n");
        sb.append("Diego.\n");
        sb.append("</ocean:if>\n");
        String result = exp.execute(sb.toString(), pageCache, null, new ArrayList<>()).replaceAll("([^\\S\\r\\n])+|(?:\\r?\\n)+", "");
        assertEquals("Tom.Penelope.Diego.", result);
    }

    @Test
    public void e() throws BlueOceanException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        PageCache pageCache = this.create();
        StringBuilder sb = new StringBuilder();
        ExperienceManager exp = new ExperienceManager();
        sb.append("<ocean:if spec=\"${not == null}\">\n");
        sb.append("not.\n");
        sb.append("</ocean:if>\n");
        sb.append("<ocean:if spec=\"${not != null}\">\n");
        sb.append("!not.\n");
        sb.append("</ocean:if>\n");
        String result = exp.execute(sb.toString(), pageCache, null,new ArrayList<>()).replaceAll("([^\\S\\r\\n])+|(?:\\r?\\n)+", "");
        assertEquals("not.", result);
    }

    @Test
    public void f() throws BlueOceanException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        PageCache pageCache = this.create();
        StringBuilder sb = new StringBuilder();
        ExperienceManager exp = new ExperienceManager();
        sb.append("<ocean:if spec=\"${todos.size() > 0}\">\n");
        sb.append(" <ocean:foreach items=\"${todos}\" var=\"tdo\">\n");
        sb.append("     <ocean:set var=\"selected\" val=\"\" \n");
        sb.append("     <ocean:if spec=\"${tdo.title == 'Exercise *1'}\">\n");
        sb.append("         <ocean:set var=\"selected\" val=\"selected\"\n");
        sb.append("     </ocean:if>\n");
        sb.append("     :${selected}:\n");
        sb.append("     <ocean:foreach items=\"${tdo.people}\" var=\"person\">\n");
        sb.append("         ${tdo.id} -> ${person.pet.name},\n");
        sb.append("     </ocean:foreach>\n");
        sb.append(" </ocean:foreach>\n");
        sb.append("</ocean:if>\n");
        sb.append("<ocean:if spec=\"${todos.size() == 0}\">\n");
        sb.append("     everyonealright.\n");
        sb.append("</ocean:if>\n");
        String result = exp.execute(sb.toString(), pageCache, null, new ArrayList<>()).replaceAll("([^\\S\\r\\n])+|(?:\\r?\\n)+", "");
        assertEquals("::0->Apache*0,0->Apache*1,0->Apache*2,:selected:1->Apache*0,1->Apache*1,1->Apache*2,::2->Apache*0,2->Apache*1,2->Apache*2,", result);
    }

}
