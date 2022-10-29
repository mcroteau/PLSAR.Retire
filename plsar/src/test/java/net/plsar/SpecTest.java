package net.plsar;

import net.plsar.model.PageCache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SpecTest extends BaseTest {

    @Test
    public void a() throws PlsarException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        PageCache pageCache = this.create();
        ExperienceManager exp = new ExperienceManager();
        StringBuilder sb = new StringBuilder();
        sb.append("<plsar:foreach items=\"${todos}\" var=\"tdo\">\n");
        sb.append("     <plsar:if spec=\"${tdo.title == 'Exercise *0'}\">\n");
        sb.append("         *ned.\n");
        sb.append("     </plsar:if>\n");
        sb.append("     <plsar:if spec=\"${tdo.title == 'Exercise *1'}\">\n");
        sb.append("         *jermaine.\n");
        sb.append("     </plsar:if>\n");
        sb.append("</plsar:foreach>\n");
        String result = exp.execute(sb.toString(), pageCache, null, new ArrayList<>()).replaceAll("([^\\S\\r\\n])+|(?:\\r?\\n)+", "");
        assertEquals("*ned.*jermaine.", result);
    }

    @Test
    public void b() throws PlsarException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        PageCache resp = this.create();
        ExperienceManager exp = new ExperienceManager();
        StringBuilder sb = new StringBuilder();
        sb.append("<plsar:if spec=\"${message != ''}\">\n");
        sb.append("${message}\n");
        sb.append("</plsar:if>\n");
        String result = exp.execute(sb.toString(), resp, null,new ArrayList<>()).trim();
        assertEquals("Strain.", result);
    }

    @Test
    public void c() throws PlsarException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        PageCache pageCache = this.create();
        StringBuilder sb = new StringBuilder();
        ExperienceManager exp = new ExperienceManager();
        sb.append("<plsar:if spec=\"${blank != ''}\">\n");
        sb.append("Nothing.\n");
        sb.append("</plsar:if>\n");
        String result = exp.execute(sb.toString(), pageCache, null,new ArrayList<>()).trim();
        assertEquals("", result);
    }

    @Test
    public void d() throws PlsarException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        PageCache pageCache = this.create();
        StringBuilder sb = new StringBuilder();
        ExperienceManager exp = new ExperienceManager();
        sb.append("<plsar:if spec=\"${tom.name == 'Tom'}\">\n");
        sb.append("Tom.\n");
        sb.append("</plsar:if>\n");
        sb.append("<plsar:if spec=\"${tom.wife.name == 'Penelope'}\">\n");
        sb.append("Penelope.\n");
        sb.append("</plsar:if>\n");
        sb.append("<plsar:if spec=\"${tom.wife.pet.name == 'Diego'}\">\n");
        sb.append("Diego.\n");
        sb.append("</plsar:if>\n");
        String result = exp.execute(sb.toString(), pageCache, null, new ArrayList<>()).replaceAll("([^\\S\\r\\n])+|(?:\\r?\\n)+", "");
        assertEquals("Tom.Penelope.Diego.", result);
    }

    @Test
    public void e() throws PlsarException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        PageCache pageCache = this.create();
        StringBuilder sb = new StringBuilder();
        ExperienceManager exp = new ExperienceManager();
        sb.append("<plsar:if spec=\"${not == null}\">\n");
        sb.append("not.\n");
        sb.append("</plsar:if>\n");
        sb.append("<plsar:if spec=\"${not != null}\">\n");
        sb.append("!not.\n");
        sb.append("</plsar:if>\n");
        String result = exp.execute(sb.toString(), pageCache, null,new ArrayList<>()).replaceAll("([^\\S\\r\\n])+|(?:\\r?\\n)+", "");
        assertEquals("not.", result);
    }

    @Test
    public void f() throws PlsarException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        PageCache pageCache = this.create();
        StringBuilder sb = new StringBuilder();
        ExperienceManager exp = new ExperienceManager();
        sb.append("<plsar:if spec=\"${todos.size() > 0}\">\n");
        sb.append(" <plsar:foreach items=\"${todos}\" var=\"tdo\">\n");
        sb.append("     <plsar:set var=\"selected\" val=\"\" \n");
        sb.append("     <plsar:if spec=\"${tdo.title == 'Exercise *1'}\">\n");
        sb.append("         <plsar:set var=\"selected\" val=\"selected\"\n");
        sb.append("     </plsar:if>\n");
        sb.append("     :${selected}:\n");
        sb.append("     <plsar:foreach items=\"${tdo.people}\" var=\"person\">\n");
        sb.append("         ${tdo.id} -> ${person.pet.name},\n");
        sb.append("     </plsar:foreach>\n");
        sb.append(" </plsar:foreach>\n");
        sb.append("</plsar:if>\n");
        sb.append("<plsar:if spec=\"${todos.size() == 0}\">\n");
        sb.append("     everyonealright.\n");
        sb.append("</plsar:if>\n");
        String result = exp.execute(sb.toString(), pageCache, null, new ArrayList<>()).replaceAll("([^\\S\\r\\n])+|(?:\\r?\\n)+", "");
        assertEquals("::0->Apache*0,0->Apache*1,0->Apache*2,:selected:1->Apache*0,1->Apache*1,1->Apache*2,::2->Apache*0,2->Apache*1,2->Apache*2,", result);
    }

}
