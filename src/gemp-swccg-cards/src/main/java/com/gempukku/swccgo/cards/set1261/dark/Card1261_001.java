package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.DuringBattleCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBePlacedInUsedPileByAnyPlayerModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import java.util.List;

/** Legacy Block 6.1 printing; original and OVS 3B3-10 have different text. */
public class Card1261_001 extends AbstractDroid {
    public Card1261_001() {
        super(Side.DARK, 2, 2, 3, 3, "3B3-10", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setVirtualSuffix(true);
        setArmor(3);
        setLore("Infantry battle droid who assumed command after his regiment's commander fell to Amidala's blaster. Re-routed Droid Control Ship order to himself in the heat of battle.");
        setGameText("While at an interior Naboo site, your [Presence] droids here are immune to We'll Handle This and (if at opponent's site) cannot be placed in Used Pile during battle. If with a unique (•) battle droid, draws one battle destiny if not able to otherwise.");
        addIcons(Icon.PRESENCE, Icon.THEED_PALACE, Icon.EPISODE_I);
        addKeywords(Keyword.INFANTRY_BATTLE_DROID);
        addModelType(ModelType.BATTLE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        var protectedDroids = Filters.and(Filters.your(self), Filters.droid, Icon.PRESENCE, Filters.here(self));
        Condition atInteriorNaboo = new AtCondition(self, Filters.interior_Naboo_site);
        Condition usedPileProtection = new AndCondition(atInteriorNaboo,
                new AtCondition(self, Filters.opponents(self)), new DuringBattleCondition());
        return List.of(
                new ImmuneToTitleModifier(self, protectedDroids, atInteriorNaboo, Title.Well_Handle_This),
                new MayNotBePlacedInUsedPileByAnyPlayerModifier(self, protectedDroids, usedPileProtection),
                new DrawsBattleDestinyIfUnableToOtherwiseModifier(self,
                        new WithCondition(self, Filters.and(Filters.unique, Filters.battle_droid)), 1));
    }
}
