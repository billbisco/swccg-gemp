package com.gempukku.swccgo.cards.set1230.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ApplyOwnDeploymentModifiersAtAnyLocationModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.IgnoresLocationDeploymentRestrictionsInGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 3
 * Type: Effect
 * Title: Commando Training
 */
public class Card1230_085 extends AbstractNormalEffect {
    public Card1230_085() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Commando_Training, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Han's Rebel strike team on the forest moon of Endor was highly trained in the use of blasters and explosives.");
        setGameText("Deploy on table. Ben Kenobi is deploy +1. Your C-3PO, Lando, and Rebels (except [Death Star II] Jedi) ignore location deployment restrictions (and 'power -1') in their game text and apply their Endor deployment modifiers at any location. Once per game, may deploy a [Virtual Block 4] Effect from Reserve Deck; reshuffle. (Immune to Alter.)");
        addIcons(Icon.ENDOR);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter yourC3POLandoAndRebelsExceptDs2Jedi = Filters.and(Filters.your(self),
                Filters.or(Filters.C3PO, Filters.Lando, Filters.and(Filters.Rebel, Filters.not(Filters.and(Filters.Jedi, Icon.DEATH_STAR_II)))));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostModifier(self, Filters.title("Ben Kenobi"), 1));
        modifiers.add(new IgnoresLocationDeploymentRestrictionsInGameTextModifier(self, yourC3POLandoAndRebelsExceptDs2Jedi));
        modifiers.add(new ApplyOwnDeploymentModifiersAtAnyLocationModifier(self, yourC3POLandoAndRebelsExceptDs2Jedi));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__COMMANDO_TRAINING_V__DEPLOY_EFFECT;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a Virtual Block 4 Effect");
            action.setActionMsg("Deploy a [Virtual Block 4] Effect from Reserve Deck");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.Effect, Icon.LEGACY_BLOCK_4), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
