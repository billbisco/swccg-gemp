package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.DuringBattleAtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.SpecialFlagModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Effect
 * Title: Entrenchment
 */
public class Card1220_120 extends AbstractNormalEffect {
    public Card1220_120() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, Title.Entrenchment, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("As the Empire advances, Rebels retreat to fortified positions to marshal their resources and redeploy the troops.");
        setGameText("Deploy on a site. During battle here, you take the first weapons segment action. Your characters are defense value +1 here.");
        addKeywords(Keyword.DEPLOYS_ON_SITE);
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.site;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new SpecialFlagModifier(self, new DuringBattleAtCondition(Filters.here(self)),
                ModifierFlag.TAKES_FIRST_BATTLE_WEAPONS_SEGMENT_ACTION, playerId));
        modifiers.add(new DefenseValueModifier(self, Filters.and(Filters.your(self), Filters.character, Filters.here(self)), 1));
        return modifiers;
    }
}
