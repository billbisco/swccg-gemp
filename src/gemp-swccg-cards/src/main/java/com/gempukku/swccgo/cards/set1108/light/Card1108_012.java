package com.gempukku.swccgo.cards.set1108.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DockingBayTransitFromForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.DockingBayTransitToForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 8
 * Type: Character
 * Subtype: Alien
 * Title: Leslomy Tacema
 */
public class Card1108_012 extends AbstractAlien {
    public Card1108_012() {
        super(Side.LIGHT, 3, 2, 1, 2, 3, Title.Leslomy_Tacema, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_8, Rarity.V);
        setVirtualSuffix(true);
        setLore("Female Duros. Expert pilot. Helps run the docking facilities in Mos Eisley. Friends with Ellorrs Madak. Approves all cargo manifests. Forgiving of minor transgressions.");
        setGameText("Adds 3 to power of anything she pilots. While on Tatooine, your docking bay transit to and from Tatooine is free. Power +2 at Cantina, Mos Eisley, or a docking bay. During your deploy phase, may deploy one Bo Shuda, Ellorrs Madak, or Yerka Mig on table from Reserve Deck; reshuffle.");
        addIcons(Icon.PILOT, Icon.JABBAS_PALACE, Icon.WARRIOR);
        addKeywords(Keyword.FEMALE);
        setSpecies(Species.DUROS);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Condition onTatooine = new OnCondition(self, Title.Tatooine);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new DockingBayTransitFromForFreeModifier(self, Filters.Tatooine_location, onTatooine, playerId));
        modifiers.add(new DockingBayTransitToForFreeModifier(self, Filters.Tatooine_location, onTatooine, playerId));
        modifiers.add(new PowerModifier(self, new AtCondition(self, Filters.or(Filters.Cantina, Filters.Mos_Eisley, Filters.docking_bay)), 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__LESLOMY_V__DEPLOY_CARD;

        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy Bo Shuda, Ellorrs Madak, or Yerka Mig from Reserve Deck");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.Bo_Shuda, Filters.Ellorrs_Madak, Filters.title(Title.Yerka_Mig)), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
