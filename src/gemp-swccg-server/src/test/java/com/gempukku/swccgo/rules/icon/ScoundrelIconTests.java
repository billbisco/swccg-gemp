package com.gempukku.swccgo.rules.icon;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ScoundrelIconTests {

    private static class TestScoundrelEffect extends AbstractNormalEffect {
        TestScoundrelEffect() {
            super(Side.LIGHT, 3, PlayCardZoneOption.ATTACHED, "Test Scoundrel Effect", Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C1);
            addIcon(Icon.SCOUNDREL);
        }
    }

    @Test
    public void ScoundrelIconHumanReadableName() {
        assertEquals("Scoundrel", Icon.SCOUNDREL.getHumanReadable());
        assertEquals("Scoundrel", Keyword.SCOUNDREL.getHumanReadable());
    }

    @Test
    public void TestEffectReportsScoundrelIconNotAsKeyword() {
        var effect = new TestScoundrelEffect();
        assertTrue(effect.hasIcon(Icon.SCOUNDREL));
        assertEquals(1, effect.getIconCount(Icon.SCOUNDREL));
        assertFalse(effect.hasKeyword(Keyword.SCOUNDREL));
    }
}
