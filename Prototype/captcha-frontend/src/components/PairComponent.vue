<template>
  <div class="PairComponent">
    <h1>PairComponent</h1>
    <p>Determine the degree of semantic relatedness of word <strong>{{ lemma }}</strong> in the following contexts </p>
    <p v-html="boldContext1"></p>
    <p v-html="boldContext2"></p>
    <div class="button-row">
      <button id="1" @click=saveButtonPress(1)>1 - Not related</button>
      <button id="2" @click=saveButtonPress(2)>2 - Distantly Related</button>
      <button id="3" @click=saveButtonPress(3)>3 - Closely Related</button>
      <button id="4" @click=saveButtonPress(4)>4 - Identical</button>
    </div>
    <p v-if="pressedButton != null">You chose: {{ pressedButton }}</p>
    <p v-else>Choose a degree of relatedness</p>
    <div class="button-submit">
      <button id="submit" @click=submitUserChoice>Submit</button>
    </div>
    <p>Current Pair: {{ currentPair }}</p>

  </div>
</template>


<script>
import PairDataService from "@/services/PairDataService";

export default {
  name: "PairComponent",
  data() {
    return {
      msg: "",
      currentPairs: [],
      currentPair: null,
      ID: null,
      lemma: null,
      context1: null,
      context2: null,
      indexes1: [-1, 0],
      indexes2: [-1, 0],
      label: 0,
      pressedButton: null
    };
  },
  methods: {
    getNext() {
      PairDataService.getNext()
        .then(response => {
          this.currentPair = response.data;
          console.log("Received:" + response.data);
          this.loadPair();
        })
        .catch(e => {
          console.log(e);
        });
    },

    loadPair() {
      if (this.currentPair != null) {
        this.lemma = this.currentPair.usage1.lemma;
        this.ID = this.currentPair.id;
        this.context1 = this.currentPair.usage1.context;
        this.context2 = this.currentPair.usage2.context;
        this.indexes1 = [this.currentPair.usage1.posStartIndex, this.currentPair.usage1.posEndIndex];
        this.indexes2 = [this.currentPair.usage2.posStartIndex, this.currentPair.usage2.posEndIndex];
        this.label = this.currentPair.label;
      }
    },
    saveButtonPress(id) {
      this.pressedButton = id;
      console.log(`Button with ID ${id} was clicked.`);
    },
    submitUserChoice() {
      console.log(`User chose ${this.pressedButton} for pair ${this.currentPair}`);
      if (this.pressedButton == null) {
        console.log("No button was pressed");

      } else {
        console.log("Submit user choice");

        let data = {
          id: this.ID,
          choice: this.pressedButton
        };

        PairDataService.postChoice(data)
          .then(response => {
            let success = response.data;
            this.$emit("submit", success);
            console.log("Received:" + response.data);
          })
          .catch(e => {
            console.log(e);
          });


      }
    }
  },
  mounted() {
    this.msg = "Mounted";
    console.log(this.msg);
    this.getNext();
  },
  computed: {
    boldContext1() {
      if (this.indexes1[0] === -1) {
        return "Context 1:" + this.context1;
      }
      let startIndex = this.indexes1[0];
      let endIndex = this.indexes1[1];
      return "Context 1:" + this.context1.slice(0, startIndex) +
        "<strong>" + this.context1.slice(startIndex, endIndex) + "</strong>" +
        this.context1.slice(endIndex, this.context1.length);
    },
    boldContext2() {
      if (this.indexes2[0] === -1) {
        return "Context 2:" + this.context1;
      }
      let startIndex = this.indexes2[0];
      let endIndex = this.indexes2[1];
      return "Context 2: " + this.context2.slice(0, startIndex) +
        "<strong>" + this.context2.slice(startIndex, endIndex) + "</strong>" +
        this.context2.slice(endIndex, this.context2.length);
    }
  }

};
</script>


<style scoped>
.button-row {
  display: flex;
  justify-content: center;
  gap: 10px;
}

.button-submit {
  margin-top: 10px;
  display: flex;
  justify-content: center;
}
</style>