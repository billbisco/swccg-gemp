package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyAndChooseInsteadEffect;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.FireWeaponFiredByForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 4
 * Type: Character
 * Subtype: Imperial
 * Title: Imperial Stormtrooper
 */
public class Card1240_009 extends AbstractImperial {
    public Card1240_009() {
        super(Side.DARK, 3, 2, 2, 2, 3, "Imperial Stormtrooper", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
        setArmor(4);
        setLore("One of the countless elite shock troops totally loyal to the Emperor. Unquestioningly follows orders. Willing to sacrifice their lives to accomplish a mission. First-strike force.");
        setGameText("Clone. Blaster Rifle deploys (and fires) for free on this trooper. When this trooper fires Blaster Rifle, may draw two weapon destiny and choose one (may take other into hand). Immune to Rebel Barrier.");
        addIcons(Icon.EPISODE_I, Icon.WARRIOR);
        addKeywords(Keyword.STORMTROOPER, Keyword.CLONE_TROOPER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToTargetModifier(self, Filters.title(Title.Blaster_Rifle), self));
        modifiers.add(new FireWeaponFiredByForFreeModifier(self, Filters.title(Title.Blaster_Rifle)));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Rebel_Barrier));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        Filter blasterRifleFiredByThisTrooper = Filters.and(Filters.title(Title.Blaster_Rifle), Filters.weaponBeingFiredBy(self));
        if (TriggerConditions.isAboutToDrawWeaponDestiny(game, effectResult, playerId, blasterRifleFiredByThisTrooper)
                && GameConditions.canDrawDestinyAndChoose(game, 1)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Draw two weapon destiny and choose one");
            action.appendEffect(
                    new DrawDestinyAndChooseInsteadEffect(action, 2, 1).setMayTakeOtherIntoHand(true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
