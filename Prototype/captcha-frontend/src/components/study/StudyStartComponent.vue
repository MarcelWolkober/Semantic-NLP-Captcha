<template>
  <div class="StudyStartComponent">
    <h1> Study </h1>
    <p> Welcome to the study of my bachelor thesis. </p>
    <!--   <p> For further information about Captchas see here: <a href="https://en.wikipedia.org/wiki/CAPTCHA" target="_blank">CAPTCHA - Wikipedia</a></p>-->
    <p> The study will take approximately 10 minutes to complete. Please do not take any breaks during the study.</p>
    <p> There will be <strong> no </strong> personal data collected, the participant answers and metrics will be stored
      anonymously. </p>
    <p>Notice: Try not to reload the page or move backwards a page or otherwise you will have to restart the study.</p>
    <p> All instruction on what to do will be given once you start. If you are ready press the button below. </p>
    <button id="startStudyButton" @click="startStudy" v-if="!buttonPressed"> Start Study</button>
    <p v-else>Waiting to receive study...</p>
  </div>
</template>

<script>
export default {
  name: "StudyStartComponent",
  data() {
    return {
      studyStarted: false,
      buttonPressed: false
    };
  },
  methods: {
    startStudy() {
      this.studyStarted = true;
      this.buttonPressed = true;
      this.$emit("studyStarted", true);
    }
  }
};

</script>

<style scoped>

</style>