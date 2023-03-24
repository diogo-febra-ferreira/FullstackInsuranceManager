<template>
  <div>
    <nav class="navbar navbar-expand-md navbar-dark bg-dark">
      <div class="container">
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNavDropdown"
          aria-controls="navbarNavDropdown" aria-expanded="false" aria-label="Toggle navigation">
          <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarNavDropdown">
          <ul class="navbar-nav mx-auto">
            <li class="nav-item">
              <nuxt-link :to="`/experts/${this.$auth.user.username}`" class="nav-link mx-2">Profile</nuxt-link>
            </li>
            <li class="nav-item">
              <nuxt-link to="/occurrences" class="nav-link mx-2">Occurrences</nuxt-link>
            </li>
            <li class="nav-item">
              <a class="nav-link mx-2 btn btn-danger" @click.prevent="signOut">Sign Out</a>
            </li>
          </ul>
        </div>
      </div>
    </nav>
    <div class="mx-auto" style="width: 600px; margin-top: 50px;">
      <b-container class="h-100">
        <b-row class="h-100">
          <b-col md="8" class="d-flex flex-column align-items-center">
            <h4>Occurrence Details: </h4>
            <p>Description: {{ occurrence.description }}</p>
            <p>Client name: {{ client.name }}</p>
            <p>Client email: {{ client.email }}</p>
            <p>Status: {{ occurrence.status }}</p>
            <p>Insurance: {{ occurrence.insurance }}</p>
            <p> Document Associated: {{ document.filename }}</p>
            <div class="custom-control-inline" style="margin-top:50px">
              <b-btn class="btn btn-primary" style="margin: 1rem;" @click.prevent="download(document)"
                tar-get="_blank">Download</b-btn>

              <button v-if="occurrence.status == 'Broken'" class="btn btn-success" style="margin: 1rem"
                v-on:click="validate('accept')"> Accept</button>
              <button v-if="occurrence.status == 'Broken'" class="btn btn-danger" style="margin: 1rem"
                v-on:click="validate('reject')"> Reject</button>

            </div>

          </b-col>
        </b-row>
      </b-container>
    </div>
  </div>
</template>

<script>
import { delay } from 'q'

export default {
  data() {
    return {
      occurrence: {},
      client: {},
      document: {}

    }

  },
  created() {
    this.$axios.$get(`api/occurrences/${this.code}`)
      .then(occurrence => {
        this.occurrence = occurrence || {}
        this.client = occurrence.client
        if (this.occurrence)
          this.document = occurrence.document
      })
  },
  computed: {
    code() {
      return this.$route.params.code
    }
  },
  methods: {
    signOut() {
      this.$auth.logout()
      this.$router.push('/')
    },
    download(doc) {

      this.$axios.$get('/api/documents/download/' + doc.id, { responseType: 'arraybuffer' })
        .then(file => {
          const url = window.URL.createObjectURL(new Blob([file]))
          const link = document.createElement('a')
          link.href = url
          link.setAttribute('download', doc.filename)
          document.body.appendChild(link)
          link.click()
        })
    },
    validate(status) {
      this.$axios.$post(`api/occurrences/${this.$auth.user.username}/${this.code}/${status}`)
        .then(() => {
          this.$toast.success("Occurrence status changed to " + status).goAway(1500)
          this.$router.go(0)
        })
        .catch(() => {
          this.$toast.error("Error changing occurrence status").goAway(3000);
        });
    }
  }
}
</script>

<style scoped>

</style>
