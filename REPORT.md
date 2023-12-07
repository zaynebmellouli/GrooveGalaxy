# C51 GrooveGalaxy Project Report

## 1. Introduction

(_Provide a brief overview of your project, including the business scenario and the main components: secure documents, infrastructure, and security challenge._)

(_Include a structural diagram, in UML or other standard notation._)

## 2. Project Development

### 2.1. Secure Document Format

#### 2.1.1. Design

First of all, we will have two types of machines: the client and the server. 

The server will have in his database the informations for each client. These informations are : the client's signature, the symmectric key of the client-server, the last nonce used with the client and the public key of his family (each client is in a family that has a public key known by the server and a private key owned by all family members, if the client doesn't have a family, he will be registered to a family of one).

The client will have the symmetric key of the client-server and the private key of his family.

**Client -> Server**: 

**Protect:** $(Pb_s(Sig_c, H(REQ), nonce), K_c(REQ))$

In this part, we will protect the communication between the client and the server. For this task, we need to fulfill authenticity of the client, integrity of the request and the freshness to ensure that the message won't be repeated by an outsider attacker. The message sent by the client is composed of two parts : 


* M1 $(Pb_s(Sig_c, H(REQ), nonce))$ : First one will encrypt with the public key of the server the client's signature (in order to identify in our data which client we're communicating with) plus the hash of the request (to compute the integrity of the meassage) plus a nonce (for freshness).


* M2 $(K_c(REQ))$: We have the request encrypted by the symmetric key of the client-server.

**Unprotect:** 
To unprotect this message, the server has to use his private key to get the signature of the client $(Pr_s(Pb_s(Sig_c, H(REQ), nonce)))$, the hash of the request and the nonce. With this, he will verify the client in his database that will contain: the previous nonce, the symmetric key of the client-server and the public key of the client's family. So, the server will be able to decrypt the message $K_c(K_c(REQ))$ and will finish by checking the integrity of the message (will hash the request and will check if it's equal to the hashed request received).

**Check:** To confirm the identity of the client. The server just have to decrypt the first part of the message M1 with his private key and verify the signature of the client. $(Pr_s(Pb_s(Sig_c, H(REQ), nonce)))$


**Server -> Client**

**Protect:** $(Pr_s(K_c(Pb_f(FILE), nonce), Sig_s))&

In this part, we will protect the communication in the other way. For this task, we need to fulfill authenticity of the server, the integrity of the message, the confidentiality and the freshness to ensure that the message won't be repeated by an outsider attacker. The message sent by the server is composed of:

First, it will encrypt at first the file with the public key of the family(so this file could be accessed by only the family members). After the result plus the nonce (for freshness) will be enrypted by the symmetric key of the client-server . And after two encryptions, we will perform the last encryption on the result plus the signature of the server using the private key of the server (this last one is to ensure authenticity).

**Unprotect:** To unprotect this message, the client has to use the public key of the server to get the whole message and the server's signature. After, he will use the symmetric key of the client-server to get the nonce and the file encrypted by the public key of the family that the client can decrypt or send it to another member of his family (this file will be stored encrypted so no one can see it or use it maliciously outside of the client's interface).

**Check:** To confirm the identity of the server. The client has to use the public key of the server to get the server's signature.

(_Outline the design of your custom cryptographic library and the rationale behind your design choices, focusing on how it addresses the specific needs of your chosen business scenario._)

(_Include a complete example of your data format, with the designed protections._)

#### 2.1.2. Implementation

(_Detail the implementation process, including the programming language and cryptographic libraries used._)

(_Include challenges faced and how they were overcome._)

### 2.2. Infrastructure

#### 2.2.1. Network and Machine Setup

(_Provide a brief description of the built infrastructure._)

(_Justify the choice of technologies for each server._)

#### 2.2.2. Server Communication Security

(_Discuss how server communications were secured, including the secure channel solutions implemented and any challenges encountered._)

(_Explain what keys exist at the start and how are they distributed?_)

### 2.3. Security Challenge

#### 2.3.1. Challenge Overview

(_Describe the new requirements introduced in the security challenge and how they impacted your original design._)

#### 2.3.2. Attacker Model

(_Define who is fully trusted, partially trusted, or untrusted._)

(_Define how powerful the attacker is, with capabilities and limitations, i.e., what can he do and what he cannot do_)

#### 2.3.3. Solution Design and Implementation

(_Explain how your team redesigned and extended the solution to meet the security challenge, including key distribution and other security measures._)

(_Identify communication entities and the messages they exchange with a UML sequence or collaboration diagram._)  

## 3. Conclusion

(_State the main achievements of your work._)

(_Describe which requirements were satisfied, partially satisfied, or not satisfied; with a brief justification for each one._)

(_Identify possible enhancements in the future._)

(_Offer a concluding statement, emphasizing the value of the project experience._)

## 4. Bibliography

(_Present bibliographic references, with clickable links. Always include at least the authors, title, "where published", and year._)

----
END OF REPORT
