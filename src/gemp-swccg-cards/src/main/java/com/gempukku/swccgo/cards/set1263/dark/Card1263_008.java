package com.gempukku.swccgo.cards.set1263.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.conditions.AloneCondition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.RemoveKeywordModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 6.3
 * Type: Character
 * Subtype: Droid
 * Title: Security Battle Droid (V)
 */
public class Card1263_008 extends AbstractDroid {
    public Card1263_008() {
        super(Side.DARK, 3, 4, 4, 4, "Security Battle Droid", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_6_3, Rarity.V);
        setVirtualSuffix(true);
        setArmor(3);
        setLore("Manufactured by the Baktoid Combat Automata, battle droids are used by the Trade Federation throughout the galaxy in order to secure and protect sites of strategic importance.");
        setGameText("At this site, all characters lose Heroic. Unless alone, draws one battle destiny if not able to otherwise.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.PRESENCE);
        addKeywords(Keyword.SECURITY_BATTLE_DROID);
        addModelType(ModelType.BATTLE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new RemoveKeywordModifier(self, Filters.and(Filters.character, Filters.atSameSite(self)), Keyword.HEROIC));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new NotCondition(new AloneCondition(self)), 1));
        return modifiers;
    }
}
