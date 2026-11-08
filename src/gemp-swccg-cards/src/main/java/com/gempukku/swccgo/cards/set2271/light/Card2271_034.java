package com.gempukku.swccgo.cards.set2271.light;

import java.time.LocalDate;
import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Original Princess Leia, Lost Scion (Virtual Block 7.1 sheet, 2010-06-18), a version of 1271_34; replaced by the PC card-list rephrase (PC-E1, by 2012-07-21). Same play. Anything Goes only (docs/legacy-versions.md). */
public class Card2271_034 extends AbstractRebel {
    public Card2271_034() {
        super(Side.LIGHT, 1, 5, 4, 5, 8, "Princess Leia, Lost Scion", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_1, Rarity.V);
        setGameText("While present at a battleground, Their Fire Has Gone Out Of The Universe flips and may not flip back. While armed with Anakin's Lightsaber, Leia's forfeit may not be reduced. Immune to attrition < 4.");
        setLegacyVersion(new LegacyVersion("1271_34", "",
                "Virtual Block 7.1 release sheet (2010-06-18)", LocalDate.of(2010, 6, 18), LocalDate.of(2012, 7, 21)));
        addPersona(Persona.LEIA);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.CLOUD_CITY);
        addKeywords(Keyword.FEMALE);
        setSpecies(Species.ALDERAANIAN);
        setMatchingWeaponFilter(Filters.Anakins_Lightsaber);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return Arrays.asList(
                new MayNotBeFlippedModifier(self, new PresentAtCondition(self, Filters.battleground), Filters.Hunt_Down_And_Destroy_The_Jedi),
                new MayNotHaveForfeitValueReducedModifier(self, self, new ArmedWithCondition(self, Filters.Anakins_Lightsaber)),
                new ImmuneToAttritionLessThanModifier(self, 4));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.isPresentAt(game, self, Filters.battleground)) {
            PhysicalCard objective = Filters.findFirstActive(game, self, Filters.Their_Fire_Has_Gone_Out_Of_The_Universe);
            if (objective != null && GameConditions.canBeFlipped(game, objective)) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Flip " + GameUtils.getFullName(objective));
                action.appendEffect(new FlipCardEffect(action, objective));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
