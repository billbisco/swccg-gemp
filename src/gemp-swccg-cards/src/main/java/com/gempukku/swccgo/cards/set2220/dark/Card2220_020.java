package com.gempukku.swccgo.cards.set2220.dark;

import java.time.LocalDate;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.cards.AbstractCreature;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.PlayCardOptionId;
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
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.FerocityModifier;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Creature
 * Title: One-Arm
 * Original version of 1220_20 (Virtual Block 2 release sheet, Dec 2009): "Wampas are [Selective Creature]", "may [download] Wampa Cave"; same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_020 extends AbstractCreature {
    public Card2220_020() {
        super(Side.DARK, 3, 6, 3, 4, 0, Title.One_Arm, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("When a wampa is wounded, the other members of its pack band together to repel the threat.");
        setGameText("Habitat: Hoth sites. Deploys only to a marker site. Wampas are [Selective Creature]. Other wampas are ferocity and defense value +2. Once per game, may deploy Wampa Cave from Reserve Deck; reshuffle.");
        setLegacyVersion(new LegacyVersion("1220_20", "",
                "Virtual Block 2 release sheet (2009-12-07), card #20; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
        addModelType(ModelType.SNOW);
        addIcons(Icon.SPECIAL_EDITION);
        addKeyword(Keyword.WAMPA);
    }

    @Override
    public boolean hasPowerAttribute() {
        return true;
    }

    @Override
    public Float getPower() {
        return 3f;
    }

    @Override
    protected Filter getGameTextHabitatFilter(String playerId, final SwccgGame game, final PhysicalCard self) {
        return Filters.Hoth_site;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.marker_site;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter otherWampas = Filters.and(Filters.wampa, Filters.not(self));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new IconModifier(self, Filters.wampa, Icon.SELECTIVE_CREATURE));
        modifiers.add(new FerocityModifier(self, otherWampas, 2));
        modifiers.add(new DefenseValueModifier(self, otherWampas, 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__ONE_ARM_V__DEPLOY_WAMPA_CAVE;
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Title.Wampa_Cave)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Wampa Cave from Reserve Deck");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Wampa_Cave, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
