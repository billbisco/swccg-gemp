package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetArmorModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Effect
 * Title: Rebel Infantry
 */
public class Card1220_133 extends AbstractNormalEffect {
    public Card1220_133() {
        super(Side.LIGHT, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Rebel_Infantry, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setLore("The Alliance lost many fine soldiers at conflicts such as the Battle of Hoth. The Rebel High Command is aware of the cost of freedom from Imperial tyranny.");
        setGameText("Deploy on table. Rebel scouts (except pilots) are troopers. Rebel troopers have armor = 4 and are immune to Physical Choke. Whenever you deploy a Rebel trooper, may immediately deploy a Blaster Rifle on that character from Reserve Deck; reshuffle. (Immune to Alter.)");
        addIcons(Icon.SPECIAL_EDITION);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter rebelScoutsExceptPilots = Filters.and(Filters.Rebel_scout, Filters.not(Filters.pilot));
        Filter rebelTroopers = Filters.and(Filters.Rebel, Filters.trooper);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new KeywordModifier(self, rebelScoutsExceptPilots, Keyword.TROOPER));
        modifiers.add(new ResetArmorModifier(self, rebelTroopers, 4));
        modifiers.add(new ImmuneToTitleModifier(self, rebelTroopers, Title.Physical_Choke));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__REBEL_INFANTRY__DEPLOY_BLASTER_RIFLE;
        Filter rebelTrooper = Filters.and(Filters.your(self), Filters.Rebel, Filters.trooper);

        if (TriggerConditions.justDeployed(game, effectResult, playerId, rebelTrooper)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Title.Blaster_Rifle, true)) {
            PhysicalCard deployed = ((PlayCardResult) effectResult).getPlayedCard();
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a Blaster Rifle");
            action.setActionMsg("Deploy a Blaster Rifle on " + GameUtils.getCardLink(deployed) + " from Reserve Deck");
            action.appendEffect(
                    new DeployCardToTargetFromReserveDeckEffect(action, Filters.title(Title.Blaster_Rifle), Filters.sameCardId(deployed), true));
            return java.util.Collections.singletonList(action);
        }
        return null;
    }
}
